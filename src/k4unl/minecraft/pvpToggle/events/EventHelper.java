package k4unl.minecraft.pvpToggle.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import k4unl.minecraft.pvpToggle.lib.SpecialChars;
import k4unl.minecraft.pvpToggle.lib.Users;
import k4unl.minecraft.pvpToggle.lib.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EventHelper {


	public static void init(){
		MinecraftForge.EVENT_BUS.register(new EventHelper());
		FMLCommonHandler.instance().bus().register(new EventHelper());
	}

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
        if (event.entityLiving instanceof EntityPlayer && event.source != null && event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.source.getEntity();
            if (Users.hasPVPEnabled(((EntityPlayer) event.source.getEntity()).getDisplayName())){
                if (Users.hasPVPEnabled(((EntityPlayer) event.entityLiving).getDisplayName())){
                    event.setCanceled(false);
                    return;
                }else{
                    event.setCanceled(true);
                    player.addChatMessage(new ChatComponentTranslation(SpecialChars.RED + "Both players must have PvP enabled!"));
                }
            }else{
                event.setCanceled(true);
                player.addChatMessage(new ChatComponentTranslation(SpecialChars.RED + "Both players must have PvP enabled!"));
            }
        }
    }


    @SubscribeEvent
    public void onPlayerDeath(PlayerDropsEvent event){
        if(Config.getBool("keepInventoryOnPVPDeath")){
            if (Users.hasPVPEnabled(((EntityPlayer) event.source.getEntity()).getDisplayName())){
                if (Users.hasPVPEnabled(((EntityPlayer) event.entityLiving).getDisplayName())){

                    NBTTagList inventory = new NBTTagList();

                    for(int currentIndex = 0; currentIndex < event.drops.size(); ++currentIndex) {
                        NBTTagCompound itemTag = new NBTTagCompound();
                        event.drops.get(currentIndex).getEntityItem().writeToNBT(itemTag);
                        inventory.appendTag(itemTag);
                    }
                    NBTTagCompound entityData = event.entityPlayer.getEntityData();
                    entityData.setTag("inventoryOnDeath", inventory);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void OnPlayerRespawn(PlayerEvent.Clone event){
        if(Config.getBool("keepInventoryOnPVPDeath")){
            if(event.wasDeath){
                NBTTagCompound entityData = event.original.getEntityData();
                NBTTagList inventory = entityData.getTagList("inventoryOnDeath", 10);
                for(int i = 0; i < inventory.tagCount(); i++){
                    ItemStack created = ItemStack.loadItemStackFromNBT(inventory.getCompoundTagAt(i));
                    event.entityPlayer.inventory.addItemStackToInventory(created);
                }
            }
        }
    }

    @SubscribeEvent
    public void loggedInEvent(PlayerLoggedInEvent event) {
        if(Config.getBool("showMessageOnLogin")){
            if(!MinecraftServer.getServer().worldServerForDimension(event.player.dimension).isRemote) {
                if(Users.hasPVPEnabled(event.player.getDisplayName())){
                    event.player.addChatMessage(new ChatComponentText("PVPToggle is enabled on this server. You have PVP currently " + SpecialChars
                      .RED + "Enabled"));
                }else{
                    event.player.addChatMessage(new ChatComponentText("PVPToggle is enabled on this server. You have PVP currently " + SpecialChars
                      .GREEN + "Disabled"));
                }
            }
        }
    }
}
