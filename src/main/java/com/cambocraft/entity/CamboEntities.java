package com.cambocraft.entity;

import com.cambocraft.util.Ids;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Entités du mod : PNJ d'artisan et gardiens de boss.
 *
 * <p>Avant cette classe, le mod ne déclarait aucune entité : les 16 profils de PNJ et les 12 boss
 * existaient uniquement sous forme de données JSON, sans jamais apparaître dans le monde. Ces deux
 * types sont volontairement simples (ils héritent de {@code VillagerEntity} et {@code PathAwareEntity})
 * pour rester chargeables sans mixin ni modèle 3D dédié.</p>
 */
public final class CamboEntities {
    /** PNJ marchand : rendu avec le modèle villageois vanilla, teinté par biome. */
    public static final EntityType<ArtisanEntity> ARTISAN = EntityType.Builder
            .create(ArtisanEntity::new, SpawnGroup.CREATURE)
            .dimensions(0.6f, 1.95f)
            .build("artisan");

    /** Gardien de boss : grande créature hostile, pilotée par {@link BossGuardianEntity}. */
    public static final EntityType<BossGuardianEntity> GARDIEN = EntityType.Builder
            .create(BossGuardianEntity::new, SpawnGroup.MONSTER)
            .dimensions(1.4f, 3.2f)
            .build("gardien");

    private CamboEntities() {}

    public static void register() {
        Registry.register(Registries.ENTITY_TYPE, Ids.id("artisan"), ARTISAN);
        Registry.register(Registries.ENTITY_TYPE, Ids.id("gardien"), GARDIEN);
    }
}
