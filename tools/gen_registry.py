#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Génère le code d'enregistrement Java et les ressources JSON des blocs et objets de Cambocraft.

Entrées  : assets_src/registry/blocks.yml, assets_src/registry/items.yml,
           assets_src/lang/*.json (traductions hors blocs/objets)
Sorties  : src/main/java/com/cambocraft/registry/CamboBlocks.java
           src/main/java/com/cambocraft/registry/CamboItems.java
           src/main/resources/assets/cambocraft/blockstates/*.json, models/block/*.json
           src/main/resources/assets/cambocraft/models/item/*.json
           src/main/resources/assets/cambocraft/lang/{fr_fr,en_us}.json
           src/main/resources/data/cambocraft/loot_table/blocks/*.json
           src/main/resources/data/cambocraft/tags/block/*.json   (mineable/*, needs_*_tool)
           assets_src/registry/rapport_registre.json              (rapport pour la validation)

Le catalogue YAML est la source de vérité : le Java généré n'est jamais modifié à la main.
Exécution : python3 tools/gen_registry.py  (idempotent, tri déterministe).
"""
import json
import os
import re
import sys

import yaml

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(RACINE, 'src', 'main', 'resources')
ASSETS = os.path.join(SRC, 'assets', 'cambocraft')
DATA = os.path.join(SRC, 'data', 'cambocraft')
JAVA = os.path.join(RACINE, 'src', 'main', 'java', 'com', 'cambocraft', 'registry')
NS = 'cambocraft'

# Correspondance des sons de bloc (BlockSoundGroup de Yarn) et des couleurs de carte (MapColor).
SONS = {
    'stone': 'BlockSoundGroup.STONE',
    'wood': 'BlockSoundGroup.WOOD',
    'bamboo': 'BlockSoundGroup.BAMBOO_WOOD',
    'cherry': 'BlockSoundGroup.CHERRY_WOOD',
    'grass': 'BlockSoundGroup.GRASS',
    'gravel': 'BlockSoundGroup.GRAVEL',
    'sand': 'BlockSoundGroup.SAND',
    'metal': 'BlockSoundGroup.METAL',
    'glass': 'BlockSoundGroup.GLASS',
    'cloth': 'BlockSoundGroup.WOOL',
    'plant': 'BlockSoundGroup.CROP',
    'nether': 'BlockSoundGroup.NETHER_WOOD',
    'copper': 'BlockSoundGroup.COPPER',
    'deepslate': 'BlockSoundGroup.DEEPSLATE',
    'mud': 'BlockSoundGroup.MUD',
    'amethyst': 'BlockSoundGroup.AMETHYST_BLOCK',
    'candle': 'BlockSoundGroup.CANDLE',
    'lantern': 'BlockSoundGroup.LANTERN',
    'vase': 'BlockSoundGroup.DECORATED_POT',
    'soul_sand': 'BlockSoundGroup.SOUL_SAND',
    'calcite': 'BlockSoundGroup.CALCITE',
}
COULEURS = {
    'stone': 'MapColor.STONE_GRAY',
    'dirt': 'MapColor.DIRT_BROWN',
    'wood': 'MapColor.OAK_TAN',
    'plant': 'MapColor.GREEN',
    'water': 'MapColor.WATER_BLUE',
    'metal': 'MapColor.IRON_GRAY',
    'sand': 'MapColor.PALE_YELLOW',
    'terracotta': 'MapColor.ORANGE',
    'red': 'MapColor.DARK_RED',
    'gold': 'MapColor.GOLD',
    'black': 'MapColor.BLACK',
    'white': 'MapColor.WHITE',
    'gray': 'MapColor.GRAY',
    'brown': 'MapColor.BROWN',
    'light_blue': 'MapColor.LIGHT_BLUE',
}
OUTILS = {'pickaxe': 'pickaxe', 'axe': 'axe', 'shovel': 'shovel', 'hoe': 'hoe', 'shears': 'shears', 'none': None}


def camel(ident):
    parts = re.split(r'[^A-Za-z0-9]+', ident)
    return '_'.join(p for p in parts if p).upper()


def or_none(valeur):
    return 'null' if valeur is None else valeur


class Erreurs:
    def __init__(self):
        self.liste = []

    def ajouter(self, message):
        self.liste.append(message)


class Bloc:
    """Description d'un bloc issue du catalogue, enrichie par les dérivés."""

    def __init__(self, ident, fr, en, mat, modele, **kw):
        self.id = ident
        self.fr = fr
        self.en = en
        self.mat = mat
        self.modele = modele
        self.hardness = kw.get('hardness', 1.0)
        self.resistance = kw.get('resistance', None)
        self.tool = kw.get('tool', 'none')
        self.niveau = kw.get('level', None)          # stone / iron / diamond : outil minimal
        self.sound = kw.get('sound', 'stone')
        self.color = kw.get('color', 'stone')
        self.luminance = kw.get('luminance', 0)
        self.rotations = kw.get('rotations', None)   # non nul : bloc orientable (face)
        self.axis = kw.get('axis', False)            # bloc à axe (bois, piliers)
        self.cutout = kw.get('cutout', False)        # rendu découpé (végétaux)
        self.no_collision = kw.get('no_collision', False)
        self.replaceable = kw.get('replaceable', False)
        self.break_instantly = kw.get('break_instantly', False)
        self.drops = kw.get('drops', 'self')         # self / nothing / autre identifiant
        self.item = kw.get('item', True)             # objet d'inventaire associé
        self.base = kw.get('base', None)             # bloc de base (dalles, escaliers…)
        self.wood_type = kw.get('wood_type', None)
        self.tint = kw.get('tint', None)
        self.oriented = kw.get('oriented', False)    # façade horizontale (facing)
        self.waterlogged = kw.get('waterlogged', False)
        self.pile = kw.get('pile', False)            # bloc empilable (riz, tuiles…)
        self.commentaire = kw.get('comment', None)

    @property
    def constante(self):
        return camel(self.id)


class Generateur:
    def __init__(self):
        self.blocs = []
        self.items = []
        self.textures = {}       # nom de texture -> spec (couleur, motif)
        self.lang_fr = {}
        self.lang_en = {}
        self.erreurs = Erreurs()
        self.vus = set()

    # -- chargement ---------------------------------------------------------
    def charger(self):
        chemin_blocs = os.path.join(RACINE, 'assets_src', 'registry', 'blocks.yml')
        chemin_items = os.path.join(RACINE, 'assets_src', 'registry', 'items.yml')
        with open(chemin_blocs, encoding='utf-8') as f:
            doc = yaml.safe_load(f)
        with open(chemin_items, encoding='utf-8') as f:
            items = yaml.safe_load(f)

        for nom, spec in (doc.get('textures') or {}).items():
            self.textures[nom] = spec

        # Ensembles : base + formes dérivées (dalle, escalier, muret…)
        for ensemble in doc.get('sets') or []:
            self.creer_ensemble(ensemble, doc)

        for bois in doc.get('woods') or []:
            self.creer_bois(bois)

        for brut in doc.get('blocks') or []:
            self.ajouter_bloc(brut)

        for brut in (items or {}).get('items') or []:
            self.items.append(brut)

        for bloc in self.blocs:
            self.lang_fr['block.%s.%s' % (NS, bloc.id)] = bloc.fr
            self.lang_en['block.%s.%s' % (NS, bloc.id)] = bloc.en
        for item in self.items:
            self.lang_fr['item.%s.%s' % (NS, item['id'])] = item['fr']
            self.lang_en['item.%s.%s' % (NS, item['id'])] = item['en']

    def creer_ensemble(self, ensemble, doc):
        """Crée le bloc plein puis ses dérivés (dalle, escalier, muret, barrière, pilier)."""
        base = dict(ensemble)
        base.setdefault('model', base.get('model', 'cube_all'))
        ident = base['id']
        self.ajouter_bloc(dict(base))
        fr_base = base.get('fr_lower') or base['fr'].lower()
        en_base = base['en']
        for forme in ensemble.get('forms') or []:
            commun = {
                'mat': base['mat'],
                'base': ident,
                'hardness': base.get('hardness', 1.0),
                'resistance': base.get('resistance'),
                'tool': base.get('tool', 'none'),
                'level': base.get('level'),
                'sound': base.get('sound', 'stone'),
                'color': base.get('color', 'stone'),
                'wood_type': base.get('wood_type'),
                'luminance': base.get('luminance', 0),
            }
            if forme == 'slab':
                commun.update(id=ident + '_slab', fr='Dalle de ' + fr_base, en=en_base + ' Slab', model='slab')
            elif forme == 'stairs':
                commun.update(id=ident + '_stairs', fr='Escalier de ' + fr_base, en=en_base + ' Stairs', model='stairs')
            elif forme == 'wall':
                commun.update(id=ident + '_wall', fr='Muret de ' + fr_base, en=en_base + ' Wall', model='wall')
            elif forme == 'fence':
                commun.update(id=ident + '_fence', fr='Barrière de ' + fr_base, en=en_base + ' Fence', model='fence')
            elif forme == 'fence_gate':
                commun.update(id=ident + '_fence_gate', fr='Portillon de ' + fr_base, en=en_base + ' Fence Gate', model='fence_gate')
            elif forme == 'pillar':
                commun.update(id=ident + '_pillar', fr='Pilier de ' + fr_base, en=en_base + ' Pillar', model='cube_column', axis=True)
            else:
                self.erreurs.ajouter('forme inconnue « %s » pour %s' % (forme, ident))
                continue
            self.ajouter_bloc(commun)
        # Textures dérivées pour les bois (tronc écorcé, planches…)
        for suffixe, motif in (ensemble.get('derives_textures') or {}).items():
            self.textures.setdefault(ident + suffixe, motif)

    def creer_bois(self, bois):
        """Crée le jeu complet de blocs d'un bois (tronc, planches, dérivés, feuilles, pousse)."""
        ident = bois['id']
        fr = bois['fr']
        en = bois['en']
        wt = bois.get('wood_type', 'OAK')
        dur = bois.get('hardness', 2.0)
        res = bois.get('resistance', dur)
        noms = {
            'log': (ident + '_log', 'Tronc de ' + fr.lower(), en + ' Log'),
            'stripped_log': (ident + '_stripped_log', 'Tronc écorcé de ' + fr.lower(), 'Stripped ' + en + ' Log'),
            'planks': (ident + '_planks', 'Planches de ' + fr.lower(), en + ' Planks'),
            'stairs': (ident + '_stairs', 'Escalier en ' + fr.lower(), en + ' Stairs'),
            'slab': (ident + '_slab', 'Dalle de ' + fr.lower(), en + ' Slab'),
            'fence': (ident + '_fence', 'Barrière en ' + fr.lower(), en + ' Fence'),
            'fence_gate': (ident + '_fence_gate', 'Portillon en ' + fr.lower(), en + ' Fence Gate'),
            'leaves': (ident + '_leaves', 'Feuilles de ' + fr.lower(), en + ' Leaves'),
            'sapling': (ident + '_sapling', 'Pousse de ' + fr.lower(), en + ' Sapling'),
        }
        planches_spec = self.textures.get(ident + '_planks', {'pattern': 'planks', 'base': '#A87A4A', 'accent': '#8B6238', 'alt': '#BE9059'})
        top_spec = self.textures.get(ident + '_log_top', planches_spec)
        if 'stripped_log' in (bois.get('forms') or []):
            self.textures.setdefault(ident + '_stripped_log_side', {
                'pattern': 'column', 'base': planches_spec['base'], 'accent': planches_spec['accent'], 'alt': planches_spec['alt']
            })
            self.textures.setdefault(ident + '_stripped_log_top', dict(top_spec))
        communes = {'mat': None, 'hardness': dur, 'resistance': res, 'tool': 'axe', 'sound': 'wood',
                    'color': bois.get('color', 'wood'), 'wood_type': wt}
        for form in bois.get('forms') or []:
            bloc_id, nom_fr, nom_en = noms[form]
            if form in ('log', 'stripped_log'):
                self.ajouter_bloc(dict(communes, id=bloc_id, fr=nom_fr, en=nom_en,
                                       mat=bloc_id, model='cube_column', axis=True, sound='wood'))
            elif form == 'planks':
                self.ajouter_bloc(dict(communes, id=bloc_id, fr=nom_fr, en=nom_en,
                                       mat=bloc_id, model='cube_all'))
            elif form in ('stairs', 'slab', 'fence', 'fence_gate'):
                self.ajouter_bloc(dict(communes, id=bloc_id, fr=nom_fr, en=nom_en,
                                       mat=ident + '_planks', model=form, base=ident + '_planks'))
            elif form == 'leaves':
                self.ajouter_bloc(dict(communes, id=bloc_id, fr=nom_fr, en=nom_en,
                                       mat=bloc_id, model='cube_all', tool='hoe', sound='grass',
                                       hardness=0.3, resistance=0.3, color='plant'))
            elif form == 'sapling':
                self.ajouter_bloc(dict(communes, id=bloc_id, fr=nom_fr, en=nom_en,
                                       mat=bloc_id, model='cross', tool='none', sound='grass',
                                       hardness=0, resistance=0, color='plant', cutout=True))
            else:
                self.erreurs.ajouter('forme de bois inconnue « %s » pour %s' % (form, ident))

    def ajouter_bloc(self, brut):
        ident = brut['id']
        if ident in self.vus:
            self.erreurs.ajouter('bloc en double : %s' % ident)
            return
        self.vus.add(ident)
        modele = brut.get('model', 'cube_all')
        self.blocs.append(Bloc(
            ident, brut.get('fr', ident), brut.get('en', ident), brut['mat'], modele,
            hardness=brut.get('hardness', 1.0),
            resistance=brut.get('resistance'),
            tool=brut.get('tool', 'none'),
            level=brut.get('level'),
            sound=brut.get('sound', 'stone'),
            color=brut.get('color', 'stone'),
            luminance=brut.get('luminance', 0),
            rotations=brut.get('rotations'),
            axis=brut.get('axis', modele == 'cube_column'),
            cutout=brut.get('cutout', modele == 'cross'),
            no_collision=brut.get('no_collision', modele == 'cross'),
            replaceable=brut.get('replaceable', False),
            break_instantly=brut.get('break_instantly', False),
            drops=brut.get('drops', 'self'),
            item=brut.get('item', True),
            base=brut.get('base'),
            wood_type=brut.get('wood_type'),
            tint=brut.get('tint'),
            oriented=brut.get('oriented', False),
            waterlogged=brut.get('waterlogged', False),
            pile=brut.get('pile', False),
            comment=brut.get('comment'),
        ))

    # -- Java ---------------------------------------------------------------
    def reglages(self, bloc):
        morceaux = ['AbstractBlock.Settings.create()']
        morceaux.append('.mapColor(%s)' % COULEURS.get(bloc.color, 'MapColor.STONE_GRAY'))
        if bloc.break_instantly:
            morceaux.append('.breakInstantly()')
        else:
            if bloc.resistance is not None:
                morceaux.append('.strength(%sf, %sf)' % (self.f(bloc.hardness), self.f(bloc.resistance)))
            else:
                morceaux.append('.strength(%sf)' % self.f(bloc.hardness))
        if bloc.no_collision:
            morceaux.append('.noCollision()')
        if bloc.cutout:
            morceaux.append('.nonOpaque()')
        if bloc.luminance:
            morceaux.append('.luminance(state -> %d)' % bloc.luminance)
        if bloc.replaceable:
            morceaux.append('.replaceable()')
        if bloc.tool != 'none':
            morceaux.append('.requiresTool()')
        morceaux.append('.sounds(%s)' % SONS.get(bloc.sound, 'BlockSoundGroup.STONE'))
        return ''.join(morceaux)

    @staticmethod
    def f(valeur):
        texte = ('%.3f' % float(valeur)).rstrip('0').rstrip('.')
        return texte if texte else '0'

    def java_bloc(self, bloc):
        reglages = self.reglages(bloc)
        const = bloc.constante
        path = bloc.id
        m = bloc.modele
        ligne = []
        if m == 'slab':
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new SlabBlock(%s), new Item.Settings());'
                         % (const, path, reglages))
        elif m == 'stairs':
            base = repr(bloc.base)
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new StairsBlock(%s.getDefaultState(), %s), new Item.Settings());'
                         % (const, path, camel(bloc.base), reglages))
        elif m == 'wall':
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new WallBlock(%s), new Item.Settings());'
                         % (const, path, reglages))
        elif m == 'fence':
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new FenceBlock(%s), new Item.Settings());'
                         % (const, path, reglages))
        elif m == 'fence_gate':
            wt = bloc.wood_type or 'OAK'
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new FenceGateBlock(WoodType.%s, %s), new Item.Settings());'
                         % (const, path, wt, reglages))
        elif m == 'carpet':
            ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new CarpetBlock(%s), new Item.Settings());'
                         % (const, path, reglages))
        elif m == 'cube_column' or bloc.axis:
            # Un bloc a axe DOIT etre un PillarBlock : sinon la propriete `axis` declaree
            # dans le blockstate n'existe pas et Minecraft rejette toutes les variantes
            # (textures invisibles / modele manquant).
            if bloc.item:
                ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", new PillarBlock(%s), new Item.Settings());'
                             % (const, path, reglages))
            else:
                ligne.append('    public static final Block %s = Registrar.block("%s", new PillarBlock(%s));'
                             % (const, path, reglages))
        else:
            if bloc.item:
                ligne.append('    public static final Block %s = Registrar.blockWithItem("%s", %s, new Item.Settings());'
                             % (const, path, reglages))
            else:
                ligne.append('    public static final Block %s = Registrar.block("%s", %s);'
                             % (const, path, reglages))
        if bloc.commentaire:
            ligne.insert(0, '    /** %s */' % bloc.commentaire)
        return '\n'.join(ligne)

    def generer_java(self):
        entetes = """package com.cambocraft.registry;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoodType;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;

/**
 * Blocs de Cambocraft.
 *
 * <p>Fichier GÉNÉRÉ par {@code tools/gen_registry.py} à partir de
 * {@code assets_src/registry/blocks.yml} : ne pas modifier à la main, régénérer.</p>
 */
public final class CamboBlocks {
    private CamboBlocks() {}
"""
        cutouts = [b for b in self.blocs if b.cutout]
        tous = ',\n'.join('            ' + b.constante for b in self.blocs)
        queue = (
            '\n    /** Tous les blocs du catalogue, dans leur ordre de déclaration. */\n'
            '    public static final List<Block> ALL = List.of(\n' + tous + '\n    );\n'
            '\n    /** Blocs à rendre en mode découpé (végétaux, fleurs, cultures) — utilisé par le client. */\n'
            '    public static final List<Block> CUTOUT = List.of(\n'
            + ',\n'.join('            ' + b.constante for b in cutouts) + '\n    );\n}\n'
        )
        corps = [entetes]
        for b in self.blocs:
            corps.append(self.java_bloc(b))
        corps.append(queue)
        sortie = '\n'.join(corps) + '\n'
        fichier = os.path.join(JAVA, 'CamboBlocks.java')
        self.ecrire(fichier, sortie)

    # -- JSON ---------------------------------------------------------------
    def ecrire(self, chemin, contenu):
        os.makedirs(os.path.dirname(chemin), exist_ok=True)
        ancien = None
        if os.path.exists(chemin):
            with open(chemin, encoding='utf-8') as f:
                ancien = f.read()
        if ancien != contenu:
            with open(chemin, 'w', encoding='utf-8') as f:
                f.write(contenu)

    def ecrire_json(self, chemin, donnees):
        self.ecrire(chemin, json.dumps(donnees, indent=2, ensure_ascii=False, sort_keys=True) + '\n')

    def texture_block(self, nom):
        return '%s:block/%s' % (NS, nom)

    def faces(self, bloc):
        """Détermine les textures (all / top / side / bottom / end) d'un bloc."""
        tex = bloc.mat
        racine = tex[:-5] if tex.endswith('_side') else tex
        if bloc.modele == 'cube_bottom_top':
            side = (racine + '_side') if (racine + '_side') in self.textures else tex
            top = (racine + '_top') if (racine + '_top') in self.textures else side
            bottom = (racine + '_bottom') if (racine + '_bottom') in self.textures else top
            return {'top': self.texture_block(top), 'side': self.texture_block(side),
                    'bottom': self.texture_block(bottom)}
        if bloc.modele == 'cube_column':
            side = (racine + '_side') if (racine + '_side') in self.textures else tex
            end = (racine + '_top') if (racine + '_top') in self.textures else side
            return {'end': self.texture_block(end), 'side': self.texture_block(side)}
        if bloc.modele == 'cross':
            return {'cross': self.texture_block(tex)}
        return {'all': self.texture_block(tex)}

    def modeles_bloc(self, bloc):
        """Écrit les fichiers de modèle du bloc et renvoie la liste (suffixe, parent, textures)."""
        modeles = []
        prefixe = '%s:block/%s' % (NS, bloc.id)
        faces = self.faces(bloc)
        m = bloc.modele
        if m in ('cube_all', 'leaves', 'cube_bottom_top'):
            parent = 'minecraft:block/cube_all' if m != 'cube_bottom_top' else 'minecraft:block/cube_bottom_top'
            modeles.append((bloc.id, parent, faces))
        elif m == 'cube_column':
            modeles.append((bloc.id, 'minecraft:block/cube_column', faces))
        elif m == 'cross':
            modeles.append((bloc.id, 'minecraft:block/cross', faces))
        elif m == 'carpet':
            modeles.append((bloc.id, 'minecraft:block/carpet', {'wool': self.texture_block(bloc.mat)}))
        elif m == 'slab':
            base_faces = {'bottom': self.texture_block(bloc.mat), 'top': self.texture_block(bloc.mat),
                          'side': self.texture_block(bloc.mat)}
            modeles.append((bloc.id, 'minecraft:block/slab', base_faces))
            modeles.append((bloc.id + '_top', 'minecraft:block/slab_top', base_faces))
        elif m == 'stairs':
            faces3 = {'bottom': self.texture_block(bloc.mat), 'top': self.texture_block(bloc.mat),
                      'side': self.texture_block(bloc.mat)}
            modeles.append((bloc.id, 'minecraft:block/stairs', faces3))
            modeles.append((bloc.id + '_inner', 'minecraft:block/inner_stairs', faces3))
            modeles.append((bloc.id + '_outer', 'minecraft:block/outer_stairs', faces3))
        elif m == 'wall':
            facesw = {'wall': self.texture_block(bloc.mat)}
            modeles.append((bloc.id + '_post', 'minecraft:block/template_wall_post', facesw))
            modeles.append((bloc.id + '_side', 'minecraft:block/template_wall_side', facesw))
            modeles.append((bloc.id + '_side_tall', 'minecraft:block/template_wall_side_tall', facesw))
        elif m == 'fence':
            facesf = {'texture': self.texture_block(bloc.mat)}
            modeles.append((bloc.id + '_post', 'minecraft:block/fence_post', facesf))
            modeles.append((bloc.id + '_side', 'minecraft:block/fence_side', facesf))
        elif m == 'fence_gate':
            facesg = {'texture': self.texture_block(bloc.mat)}
            modeles.append((bloc.id, 'minecraft:block/template_fence_gate', facesg))
            modeles.append((bloc.id + '_open', 'minecraft:block/template_fence_gate_open', facesg))
            modeles.append((bloc.id + '_wall', 'minecraft:block/template_fence_gate_wall', facesg))
            modeles.append((bloc.id + '_wall_open', 'minecraft:block/template_fence_gate_wall_open', facesg))
        else:
            self.erreurs.ajouter('modèle inconnu « %s » pour %s' % (m, bloc.id))
            return []
        for nom, parent, textures in modeles:
            self.ecrire_json(os.path.join(ASSETS, 'models', 'block', nom + '.json'),
                             {'parent': parent, 'textures': textures})
        return modeles

    def blockstate(self, bloc):
        ident = '%s:block/%s' % (NS, bloc.id)
        m = bloc.modele
        variantes = {}
        if m == 'cube_column':
            for axe in ('x', 'y', 'z'):
                variantes['axis=' + axe] = {'model': ident, 'x': 90 if axe == 'x' else (0 if axe == 'y' else 0),
                                            'y': 90 if axe == 'z' else 0}
        elif m == 'slab':
            # La dalle double affiche le modèle plein de sa matière (jamais la géométrie de dalle).
            plein = '%s:block/%s' % (NS, bloc.base or bloc.mat)
            variantes = {
                'type=bottom': {'model': ident},
                'type=top': {'model': ident + '_top'},
                'type=double': {'model': plein},
            }
        elif m == 'stairs':
            for face, y in (('east', 0), ('north', 90), ('south', 180), ('west', 270)):
                for half in ('bottom', 'top'):
                    for forme, suffixe in (('straight', ''), ('inner_left', '_inner'), ('inner_right', '_inner'),
                                           ('outer_left', '_outer'), ('outer_right', '_outer')):
                        rotation = y
                        if forme == 'inner_left' or forme == 'outer_left':
                            rotation = (y + 270) % 360
                        entree = {'model': ident + suffixe}
                        if rotation:
                            entree['y'] = rotation
                        if suffixe:
                            entree['uvlock'] = True
                        variantes['facing=%s,half=%s,shape=%s' % (face, half, forme)] = entree
        elif m == 'wall':
            self.blockstate_muret(bloc, ident)
            return
        elif m == 'fence':
            multipart = [{'apply': {'model': ident + '_post'}}]
            for face, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270)):
                apply = {'model': ident + '_side', 'uvlock': True}
                if y:
                    apply['y'] = y
                multipart.append({'apply': apply, 'when': {face: 'true'}})
            self.ecrire_json(os.path.join(ASSETS, 'blockstates', bloc.id + '.json'), {'multipart': multipart})
            return
        elif m == 'fence_gate':
            for face, y in (('east', 270), ('north', 180), ('south', 0), ('west', 90)):
                for ouvert in ('false', 'true'):
                    for mur in ('false', 'true'):
                        nom = ident + ('_open' if ouvert == 'true' else '')
                        if mur == 'true':
                            nom = ident + '_wall' + ('_open' if ouvert == 'true' else '')
                        entree = {'model': nom, 'uvlock': True}
                        if y:
                            entree['y'] = y
                        variantes['facing=%s,in_wall=%s,open=%s' % (face, mur, ouvert)] = entree
        elif m == 'cross' or m == 'carpet':
            variantes = {'': {'model': ident}}
        else:
            variantes = {'': {'model': ident}}
        self.ecrire_json(os.path.join(ASSETS, 'blockstates', bloc.id + '.json'), {'variants': variantes})

    def blockstate_muret(self, bloc, ident):
        multipart = [{'apply': {'model': ident + '_post'}, 'when': {'up': 'true'}}]
        for face, y in (('north', 0), ('east', 90), ('south', 180), ('west', 270)):
            for niveau in ('low', 'tall'):
                apply = {'model': ident + '_side' + ('_tall' if niveau == 'tall' else ''), 'uvlock': True}
                if y:
                    apply['y'] = y
                multipart.append({'apply': apply, 'when': {face: niveau}})
        self.ecrire_json(os.path.join(ASSETS, 'blockstates', bloc.id + '.json'), {'multipart': multipart})

    def butin(self, bloc):
        if bloc.item is False or bloc.drops == 'nothing':
            return None
        if bloc.drops == 'self':
            entree = '%s:%s' % (NS, bloc.id)
        else:
            entree = bloc.drops
        return {
            'type': 'minecraft:block',
            'pools': [{
                'rolls': 1,
                'bonus_rolls': 0,
                'entries': [{'type': 'minecraft:item', 'name': entree}],
                'conditions': [{'condition': 'minecraft:survives_explosion'}],
            }],
            'random_sequence': '%s:blocks/%s' % (NS, bloc.id),
        }

    def generer_json(self):
        balises = {}
        for bloc in self.blocs:
            self.modeles_bloc(bloc)
            self.blockstate(bloc)
            if bloc.item:
                if bloc.modele == 'wall':
                    modele_item = {'parent': 'minecraft:block/wall_inventory',
                                   'textures': {'wall': self.texture_block(bloc.mat)}}
                elif bloc.modele == 'fence':
                    modele_item = {'parent': 'minecraft:block/fence_inventory',
                                   'textures': {'texture': self.texture_block(bloc.mat)}}
                elif bloc.modele == 'cross':
                    modele_item = {'parent': 'minecraft:item/generated',
                                   'textures': {'layer0': self.texture_block(bloc.mat)}}
                else:
                    modele_item = {'parent': '%s:block/%s' % (NS, bloc.id)}
                self.ecrire_json(os.path.join(ASSETS, 'models', 'item', bloc.id + '.json'), modele_item)
            table = self.butin(bloc)
            if table:
                self.ecrire_json(os.path.join(DATA, 'loot_table', 'blocks', bloc.id + '.json'), table)
            outil = OUTILS.get(bloc.tool)
            if outil:
                balises.setdefault('mineable/' + outil, []).append('%s:%s' % (NS, bloc.id))
            if bloc.niveau:
                balises.setdefault('needs_%s_tool' % bloc.niveau, []).append('%s:%s' % (NS, bloc.id))
        for nom, valeurs in balises.items():
            self.ecrire_json(os.path.join(DATA, 'tags', 'block', nom + '.json'), {'replace': False, 'values': sorted(valeurs)})
        self.generer_items()
        self.generer_lang()

    def generer_items(self):
        lignes = []
        for item in self.items:
            ident = item['id']
            reglages = ['new Item.Settings()']
            if item.get('max_count'):
                reglages.append('.maxCount(%d)' % item['max_count'])
            if item.get('rarity'):
                reglages.append('.rarity(Rarity.%s)' % item['rarity'].upper())
            if item.get('food'):
                reglages.append('.food(%s)' % item['food'])
            if item.get('fireproof'):
                reglages.append('.fireproof()')
            if item.get('attack'):
                reglages.append('.attributeModifiers(CamboWeapons.attack(%sf, %sf))'
                                % (self.f(item['attack']), self.f(item.get('speed', -2.0))))
            reglages_src = ''.join(reglages)
            classe = item.get('class', 'Item')
            if item.get('factory'):
                lignes.append('    public static final Item %s = Registrar.item("%s", %s, %s);'
                              % (camel(ident), ident, item['factory'], reglages_src))
            elif classe == 'Item':
                lignes.append('    public static final Item %s = Registrar.item("%s", %s);' % (camel(ident), ident, reglages_src))
            else:
                lignes.append('    public static final Item %s = Registrar.item("%s", %s::new, %s);' % (camel(ident), ident, classe, reglages_src))
            parent = item.get('parent', 'item/handheld' if item.get('held') else 'item/generated')
            texture = item.get('texture', ident)
            self.ecrire_json(os.path.join(ASSETS, 'models', 'item', ident + '.json'),
                             {'parent': 'minecraft:' + parent, 'textures': {'layer0': '%s:item/%s' % (NS, texture)}})
        entete = '''package com.cambocraft.registry;

import com.cambocraft.craft.CamboFoods;
import com.cambocraft.craft.CamboWeapons;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

/**
 * Objets de Cambocraft (hors blocs).
 *
 * <p>Fichier GÉNÉRÉ par {@code tools/gen_registry.py} à partir de
 * {@code assets_src/registry/items.yml} : ne pas modifier à la main, régénérer.</p>
 */
public final class CamboItems {
    private CamboItems() {}
'''
        fin = ['', '    /** Tous les objets du catalogue, pratique pour la vérification et les commandes. */',
               '    public static final java.util.List<Item> ALL = java.util.List.of(']
        fin.append(',\n'.join('            %s' % camel(i['id']) for i in self.items))
        fin.append('    );')
        self.ecrire(os.path.join(JAVA, 'CamboItems.java'), entete + '\n'.join(lignes) + '\n' + '\n'.join(fin) + '\n}\n')

    def generer_lang(self):
        for dossier in ('lang',):
            base = os.path.join(RACINE, 'assets_src', dossier)
            if os.path.isdir(base):
                for fichier in sorted(os.listdir(base)):
                    if not fichier.endswith('.json'):
                        continue
                    with open(os.path.join(base, fichier), encoding='utf-8') as f:
                        extra = json.load(f)
                    cible = self.lang_fr if 'fr' in fichier else self.lang_en
                    for cle, valeur in extra.items():
                        if cle in cible:
                            self.erreurs.ajouter('clé de langue en double : %s (%s)' % (cle, fichier))
                            continue
                        cible[cle] = valeur
        self.ecrire_json(os.path.join(ASSETS, 'lang', 'fr_fr.json'), self.lang_fr)
        self.ecrire_json(os.path.join(ASSETS, 'lang', 'en_us.json'), self.lang_en)

    def rapport(self):
        rapport = {
            'blocs': len(self.blocs),
            'objets': len(self.items) + sum(1 for b in self.blocs if b.item),
            'blocs_avec_objet': sum(1 for b in self.blocs if b.item),
            'textures': sorted(self.textures),
            'erreurs': self.erreurs.liste,
        }
        self.ecrire_json(os.path.join(RACINE, 'assets_src', 'registry', 'rapport_registre.json'), rapport)
        return rapport


def main():
    g = Generateur()
    g.charger()
    if g.erreurs.liste:
        print('Erreurs de catalogue :')
        for e in g.erreurs.liste:
            print(' -', e)
        return 2
    g.generer_java()
    g.generer_json()
    rapport = g.rapport()
    print('registre : %d blocs, %d objets, %d textures' % (rapport['blocs'], rapport['objets'], len(rapport['textures'])))
    return 0


if __name__ == '__main__':
    sys.exit(main())
