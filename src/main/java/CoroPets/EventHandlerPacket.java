package CoroPets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import CoroUtil.packet.PacketHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHandlerPacket {

	@SideOnly(Side.CLIENT)
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
	
	@SubscribeEvent
	public void onPacketFromServer(FMLNetworkEvent.ClientCustomPacketEvent event) {
		
		try {
			NBTTagCompound nbt = PacketHelper.readNBTTagCompound(event.packet.payload());
			
			String command = nbt.getString("command");
			
			cpw.mods.fml.common.FMLLog.info("CoroPets packet command from server: " + command);
			
		} catch (Exception ex) {
			cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)ex, "CoroUtil stacktrace: %s", (Throwable)ex);
		}
		
	}
	
	@SubscribeEvent
	public void onPacketFromClient(FMLNetworkEvent.ServerCustomPacketEvent event) {
		EntityPlayer entP = ((NetHandlerPlayServer)event.handler).playerEntity;
		
		try {
			NBTTagCompound nbt = PacketHelper.readNBTTagCompound(event.packet.payload());
			
			String command = nbt.getString("command");
			
			cpw.mods.fml.common.FMLLog.info("CoroPets packet command from client: " + command);
			
			
		} catch (Exception ex) {
			cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)ex, "CoroUtil stacktrace: %s", (Throwable)ex);
		}
	}
	
}
