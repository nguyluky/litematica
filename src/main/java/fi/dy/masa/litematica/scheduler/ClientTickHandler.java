package fi.dy.masa.litematica.scheduler;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.EntityUtils;
import net.minecraft.client.Minecraft;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.selection.SelectionManager;
import fi.dy.masa.litematica.util.EasyPlaceUtils;
import fi.dy.masa.litematica.util.WorldUtils;

public class ClientTickHandler implements IClientTickHandler
{
    private int autoSaveTimer;

    @Override
    public void onClientTick(Minecraft mc)
    {
        if (mc.level != null && mc.player != null)
        {
            SelectionManager sm = DataManager.getSelectionManager();

            if (sm.hasGrabbedElement())
            {
                sm.moveGrabbedElement(mc.player);
            }

            if (mc.screen == null)
            {
                if (Configs.Generic.EASY_PLACE_POST_REWRITE.getBooleanValue())
                {
                    EasyPlaceUtils.easyPlaceOnUseTick();
                }
                else
                {
                    WorldUtils.easyPlaceOnUseTick(mc);
                }
            }

            if (Configs.Generic.LAYER_MODE_DYNAMIC.getBooleanValue())
            {
                DataManager.getRenderLayerRange().setSingleBoundaryToPosition(EntityUtils.getCameraEntity());
            }

            DataManager.getSchematicPlacementManager().onClientTick(mc);
            TaskScheduler.getInstanceClient().runTasks();

            if (Configs.Generic.AUTO_SAVE.getBooleanValue())
            {
                int intervalTicks = Math.max(1, Configs.Generic.AUTO_SAVE_INTERVAL_SECONDS.getIntegerValue() * 20);

                if (++this.autoSaveTimer >= intervalTicks)
                {
                    DataManager.save(true);
                    this.autoSaveTimer = 0;
                }
            }
            else
            {
                this.autoSaveTimer = 0;
            }
        }
        else
        {
            this.autoSaveTimer = 0;
        }
    }
}
