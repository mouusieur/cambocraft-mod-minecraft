package com.cambocraft.data;

import com.cambocraft.util.DataBundle;

/** Sous-système qui relit ses données à chaque chargement (démarrage ou {@code /reload}). */
public interface DataConsumer {
    /** Nom court utilisé dans les journaux, par exemple {@code "pêche"}. */
    String nom();

    /** Relit les données ; les erreurs doivent être consignées, jamais lever d'exception. */
    void charger(DataBundle bundle);
}
