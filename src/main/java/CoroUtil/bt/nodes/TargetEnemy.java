package CoroUtil.bt.nodes;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import CoroUtil.bt.AIBTAgent;
import CoroUtil.bt.Behavior;
import CoroUtil.bt.BlackboardBase;
import CoroUtil.bt.EnumBehaviorState;
import CoroUtil.bt.IBTAgent;
import CoroUtil.bt.selector.Selector;

import org.bogdang.modifications.random.XSTR;

public class TargetEnemy extends Selector {

	//0 = nothing to attack, 1 = attacking, 2 = sanity check says no
	//no longer forces a moveto
	
	public IBTAgent entInt;
	public EntityLiving ent;
	
	public float rangeHunt = 16;
	public ChunkCoordinates holdPos = null; //if not null, center scan and best target scan is based from this instead of entity, shouldnt cancel active target, and should have a range higher than enemy projectile ranges
	//public float rangeStray = 8;
	public int scanRate = -1;
	public int randRate = -1;
	
	public TargetEnemy(Behavior parParent, IBTAgent parEnt, float parRange, ChunkCoordinates parHoldPos, int parScanRate, int parRandRate) {
		super(parParent);
		entInt = parEnt;
		ent = (EntityLiving)parEnt;
		rangeHunt = parRange;
		holdPos = parHoldPos;
		scanRate = parScanRate;
		randRate = parRandRate;
		//rangeStray = parStray;
	}
	
	public boolean sanityCheck(Entity target) {
		/*if (ent.getHealth() < ent.getMaxHealth() / 4F * 2) {
			return false;
		}*/
		return true;
	}

	@Override
	public EnumBehaviorState tick() {
		
		//TEMP!
		//rangeHunt = 16;
		
		boolean xRay = false;
		
		EntityLivingBase protectEnt = ent;
		Random rand = new XSTR();
		
		AIBTAgent ai = entInt.getAIBTAgent();
		
		if ((scanRate == -1 || ent.worldObj.getTotalWorldTime() % scanRate == 0) && (ai.blackboard.getTarget() == null || (randRate == -1 || rand.nextInt(randRate) == 0))) {
			boolean found = false;
			boolean sanityAborted = false;
			Entity clEnt = null;
			float closest = 9999F;
	    	List list = null;
	    	if (holdPos != null) {
	    		list = ent.worldObj.getEntitiesWithinAABBExcludingEntity(ent, AxisAlignedBB.getBoundingBox(holdPos.posX, holdPos.posY, holdPos.posZ, holdPos.posX, holdPos.posY, holdPos.posZ).expand(rangeHunt*2, rangeHunt/2, rangeHunt*2));
	    	} else {
	    		list = ent.worldObj.getEntitiesWithinAABBExcludingEntity(ent, protectEnt.boundingBox.expand(rangeHunt*2, rangeHunt/2, rangeHunt*2));
	    	}
	        for(int j = 0; j < list.size(); j++)
	        {
	            Entity entity1 = (Entity)list.get(j);
	            if(ai.isEnemy(entity1))
	            {
	            	if (xRay || ((EntityLivingBase) entity1).canEntityBeSeen(protectEnt)) {
	            		if (sanityCheck(entity1)/* && entity1 instanceof EntityPlayer*/) {
	            			float dist = 0;// = protectEnt.getDistanceToEntity(entity1);
	            			/*if (holdPos != null) {
	            				dist = (float) entity1.getDistance(holdPos.posX, holdPos.posY, holdPos.posZ);
	            			} else {*/
	            				dist = protectEnt.getDistanceToEntity(entity1);
	            			//}
	            			//cpw.mods.fml.common.FMLLog.info("dist: " + dist);
	            			if (dist < closest && dist < rangeHunt) {
	            				closest = dist;
	            				clEnt = entity1;
	            			}
	            		} else {
	            			sanityAborted = true;
	            		}
	            	}
	            }
	        }
	        if (clEnt != null) {
	        	Entity curTarg = ai.blackboard.getTarget();
	        	if (clEnt != curTarg) {
	        		ai.blackboard.setTarget(clEnt);
	        		//ai.blackboard.trackTarget(true);
	        	} else {
	        		//ai.blackboard.trackTarget(false);
	        	}
	        	//ai.huntTarget(clEnt);
	        	//cpw.mods.fml.common.FMLLog.info("hunting");
	        	if (children.size() > 1) return children.get(1).tick();
	        } else {
	        	if (!sanityAborted) {
	        		//cpw.mods.fml.common.FMLLog.info("subjob");
	        		if (children.size() > 0) return children.get(0).tick();
	        	} else {
	        		//cpw.mods.fml.common.FMLLog.info("fleeing");
	        		if (children.size() > 2) return children.get(2).tick();
	        	}
	        }
		} else {
			if (ai.blackboard.getTarget() != null) {
				//ai.blackboard.trackTarget(false);
				if (children.size() > 1) return children.get(1).tick();
			}
		}
		
		if (ai.blackboard.getTarget() == null && sanityCheck(null)) {
			//cpw.mods.fml.common.FMLLog.info("subjob");
			if (children.size() > 0) return children.get(0).tick();
		}
		
		if (!sanityCheck(null)) {
			if (children.size() > 2) return children.get(2).tick();
		}
		
		return super.tick();
	}
	
}
