package CoroUtil.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CoroUtilPath {

	public static PathEntity getSingleNodePath(ChunkCoordinates coords) {
		PathPoint points[] = new PathPoint[1];
        points[0] = new PathPoint(coords.posX, coords.posY, coords.posZ);
		PathEntity pe = new PathEntity(points);
		return pe;
	}
	
	public static boolean tryMoveToEntityLivingLongDist(EntityCreature entSource, Entity entityTo, double moveSpeedAmp) {
		return tryMoveToXYZLongDist(entSource, entityTo.posX, entityTo.boundingBox.minY, entityTo.posZ, moveSpeedAmp);
	}
	
	public static boolean tryMoveToXYZLongDist(EntityCreature ent, double x, double y, double z, double moveSpeedAmp) {
		
		World world = ent.worldObj;
		
		boolean success = false;
		
		if (ent.getNavigator().noPath()) {
			
			double distToPlayer = ent.getDistance(x, y, z);//ent.getDistanceToEntity(player);
			
			double followDist = ent.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
			
			if (distToPlayer <= followDist) {
				//boolean success = ent.getNavigator().tryMoveToEntityLiving(player, moveSpeedAmp);
				success = ent.getNavigator().tryMoveToXYZ(x, y, z, moveSpeedAmp);
				//System.out.println("success? " + success + "- move to player: " + ent + " -> " + player);
			} else {
		        /*int x = MathHelper.floor_double(player.posX);
		        int y = MathHelper.floor_double(player.posY);
		        int z = MathHelper.floor_double(player.posZ);*/
		        
		        double d = x+0.5F - ent.posX;
		        double d2 = z+0.5F - ent.posZ;
		        double d1;
		        d1 = y+0.5F - (ent.posY + (double)ent.getEyeHeight());
		        
		        double d3 = MathHelper.sqrt_double(d * d + d2 * d2);
		        float f2 = (((float)Math.atan2(d2, d) * 180F) / 3.1415927410125732F) - 90F;
		        float f3 = (-(((float)Math.atan2(d1, d3) * 180F) / 3.1415927410125732F));
		        float rotationPitch = -f3;//-ent.updateRotation(rotationPitch, f3, 180D);
		        float rotationYaw = f2;//updateRotation(rotationYaw, f2, 180D);
		        
		        EntityLiving center = ent;
		        
		        Random rand = world.rand;
		        
		        float randLook = rand.nextInt(90)-45;
		        //int height = 10;
		        double dist = (followDist * 0.75D) + rand.nextInt((int)followDist / 2);//rand.nextInt(26)+(queue.get(0).retryState * 6);
		        int gatherX = (int)Math.floor(center.posX + ((double)(-Math.sin((rotationYaw+randLook) / 180.0F * 3.1415927F)/* * Math.cos(center.rotationPitch / 180.0F * 3.1415927F)*/) * dist));
		        int gatherY = (int)center.posY;//Math.floor(center.posY-0.5 + (double)(-MathHelper.sin(center.rotationPitch / 180.0F * 3.1415927F) * dist) - 0D); //center.posY - 0D;
		        int gatherZ = (int)Math.floor(center.posZ + ((double)(Math.cos((rotationYaw+randLook) / 180.0F * 3.1415927F)/* * Math.cos(center.rotationPitch / 180.0F * 3.1415927F)*/) * dist));
		        
		        Block block = world.getBlock(gatherX, gatherY, gatherZ);
		        int tries = 0;
		        if (!CoroUtilBlock.isAir(block)) {
		        	int offset = -5;
			        
			        while (tries < 30) {
			        	if (CoroUtilBlock.isAir(block) || !block.isSideSolid(world, gatherX, gatherY, gatherZ, ForgeDirection.UP)) {
			        		break;
			        	}
			        	gatherY += offset++;
			        	block = world.getBlock(gatherX, gatherY, gatherZ);
			        	tries++;
			        }
		        } else {
		        	//int offset = 0;
		        	while (tries < 30) {
		        		if (!CoroUtilBlock.isAir(block) && block.isSideSolid(world, gatherX, gatherY, gatherZ, ForgeDirection.UP)) break;
		        		gatherY -= 1;//offset++;
		        		block = world.getBlock(gatherX, gatherY, gatherZ);
			        	tries++;
		        	}
		        }
		        
		        if (tries < 30) {
		        	success = ent.getNavigator().tryMoveToXYZ(gatherX, gatherY, gatherZ, moveSpeedAmp);
		        	//System.out.println("pp success? " + success + "- move to player: " + ent + " -> " + player);
		        }
			}
		}
		
		return success;
	}
	
}
