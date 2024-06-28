package com.solidsilver.papermc

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.persistence.PersistentDataType

class CompressListener(private val plugin: Compressor): Listener {


    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (event.itemInHand.itemMeta.persistentDataContainer.has(plugin.getCompLevelKey())) {
            event.setBuild(false)
            event.player.sendMessage("You can't build with that! It's compressed.")
        }
    }

    @EventHandler
    fun asCraft(event: PrepareItemCraftEvent) {
            var numCompItems = 0
            var cmpLvl = 0
            var itemType: Material? = null
            for (item in event.inventory.matrix) {
                if (item?.itemMeta?.persistentDataContainer?.has(plugin.getCompLevelKey()) == true) {
                    val itmLvl = item.itemMeta.persistentDataContainer.get(
                        plugin.getCompLevelKey(),
                        PersistentDataType.INTEGER
                    ) ?: 0
                    if (itemType == null) {
                        itemType = item.type
                        cmpLvl = itmLvl
                    } else if (cmpLvl != itmLvl || itemType != item.type) {
                        break
                    }
                    numCompItems += 1
                } else {
                    break
                }
            }
            if (numCompItems == 9 && cmpLvl < Compressor.MAX_LEVEL) {
                event.inventory.result = itemType?.let { plugin.createCompressedItem(it, cmpLvl + 1) }
            } else if (numCompItems in 2..8 ) {
                event.inventory.result = null
            }
    }
}