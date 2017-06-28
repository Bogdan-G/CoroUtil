package CoroUtil.world.player;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import CoroUtil.util.BlockCoord;

public class MobEnhancer {

	public static void processMobEnhancements(EntityCreature ent, float difficultyScale) {
		if (ent instanceof EntityZombie) {
			EntityZombie zombie = (EntityZombie) ent;
			zombie.setChild(false);
		}
		
		//extra xp
		try {
			int xp = ObfuscationReflectionHelper.getPrivateValue(EntityLiving.class, ent, "field_70728_aV", "experienceValue");
			xp += difficultyScale * 10F;
			ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, ent, xp, "field_70728_aV", "experienceValue");
		} catch (Exception e) {
			cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "CoroUtil stacktrace: %s", (Throwable)e);
		}
	}
	
}
