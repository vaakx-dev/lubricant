package wd40.vaakx.cog.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * A do-nothing entity used to demonstrate {@link wd40.lubricant.api.entity.EntityData}.
 *
 * <p>No renderer is registered; the entity is invisible at runtime. Spawn one
 * with {@code /summon cog:spinning_cog}, attach data with
 * {@code EntityData.set(...)}, observe persistence across save+reload.</p>
 */
public final class SpinningCog extends Entity {

    public SpinningCog(EntityType<? extends SpinningCog> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
