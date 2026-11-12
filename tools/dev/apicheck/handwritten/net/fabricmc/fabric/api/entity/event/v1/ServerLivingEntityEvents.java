package net.fabricmc.fabric.api.entity.event.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

/** Événements des entités vivantes (stub de compilation). */
public final class ServerLivingEntityEvents {
	private ServerLivingEntityEvents() {
	}

	public static final Event<AllowDamage> ALLOW_DAMAGE = null;
	public static final Event<AfterDeath> AFTER_DEATH = null;

	public interface AllowDamage {
		boolean allowDamage(LivingEntity entity, DamageSource source, float amount);
	}

	public interface AfterDeath {
		void afterDeath(LivingEntity entity, DamageSource damageSource);
	}
}
