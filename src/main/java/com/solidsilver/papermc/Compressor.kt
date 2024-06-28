package com.solidsilver.papermc

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.math.BigInteger
import java.util.*
import java.util.logging.Level

class Compressor: JavaPlugin(), Listener {
    private val compLevelKey = NamespacedKey(this, "level")

    companion object {
        const val MAX_LEVEL = 32

    }

    fun getCompLevelKey(): NamespacedKey {
        return compLevelKey
    }

    override fun onEnable() {
        super.onEnable()
        server.pluginManager.registerEvents(CompressListener(this), this)
        Bukkit.getPluginManager().registerEvents(this, this)
        val blocks = listOf(Material.COBBLESTONE,
            Material.COBBLED_DEEPSLATE,
            Material.GOLD_BLOCK,
            Material.STONE,
            Material.QUARTZ_BLOCK,
            Material.IRON_BLOCK,
            Material.DIORITE,
            Material.ANDESITE)
        logger.log(Level.INFO, "Enabling compressed blocks: ${blocks.joinToString(", ") { it.name.lowercase() }}")
        createAndAddBaseRecipes(blocks)
        CompressCommand.registerSelf(this)
    }

    private fun createAndAddBaseRecipes(types: List<Material>) {
        for (type in types) {
            server.addRecipe(this.createCompressedRecipe(type, 1))
            for (i in 1..MAX_LEVEL) {
//                server.addRecipe(this.createCompressedRecipe(type, i))
                server.addRecipe(this.createUncompressedRecipe(type, i))
            }
        }
    }

    private fun createCompressedRecipe(type: Material, level: Int): ShapedRecipe {
        val compressedItem = createCompressedItem(type, level)
        val name = type.name.split('_').joinToString(separator = " ") { it ->
            it.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        val levelKey = keyName(level, name) + "_recipe"

        val compKey = NamespacedKey(this, levelKey)

        val compRecipe = ShapedRecipe(compKey, compressedItem)
        compRecipe.shape("CCC","CCC","CCC")

        if (level == 1) {
            compRecipe.setIngredient('C', type)
        } else {
            val newItem = createCompressedItem(type, level - 1)
            compRecipe.setIngredient('C', ExactChoice(newItem))

        }
        return compRecipe
    }

    private fun createUncompressedRecipe(type: Material, level: Int): ShapelessRecipe {
        val unCompItem = if (level == 1) {
            ItemStack(type, 9)
        } else {
            createCompressedItem(type,level - 1, 9)
        }
        val name = type.name.split('_').joinToString(separator = " ") { it ->
            it.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        val levelKey = keyName(level - 1, name) + "_back_recipe"
        val unCompKey = NamespacedKey(this, levelKey)
        val recipe = ShapelessRecipe(unCompKey, unCompItem)
        recipe.addIngredient(ExactChoice(createCompressedItem(type, level)))
        return recipe
    }

    fun createCompressedItem(type: Material, level: Int, count: Int = 1): ItemStack {
        if (level == 0) {
            return ItemStack(type, count)
        }

        val name = type.name.split('_').joinToString(separator = " ") { it ->
            it.lowercase(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
        val baseAmt = BigInteger("9").pow(level)
        val compressedStack = ItemStack(type, count)
        val compMeta = compressedStack.itemMeta
        compMeta.displayName(Component.text(compressedName(level, name)))
        val realCount = baseAmt.toString()
        compMeta.lore(listOf(Component.text("$realCount $name")))
        compMeta.placeableKeys

        compMeta.addEnchant(Enchantment.DURABILITY, level, true)

        compMeta.persistentDataContainer.set(compLevelKey, PersistentDataType.INTEGER, level)
        compressedStack.setItemMeta(compMeta)
        compressedStack.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        return compressedStack
    }


}



fun realCountNice(count: BigInteger): String {
    var retCount = count
    val thou = BigInteger.valueOf(1000)
    val bar = BigInteger.valueOf(1000)
    if (count < bar) {
        return retCount.toString()
    }
    retCount = count.divide(thou)
    bar.multiply(thou)
    if (count < bar) {
        return "~${retCount}K"
    }
    retCount = count.divide(thou)
    bar.multiply(thou)
    if (count < bar) {
        return "~${retCount}M"
    }
    retCount = count.divide(thou)
    bar.multiply(thou)
    if (count < bar) {
        return "~${retCount}B"
    }
    retCount = count.divide(thou)
    bar.multiply(thou)
    if (count < bar) {
        return "~${retCount}T"
    }
    return "Countless"
}

fun compressedName(level: Int, itemName: String): String {
    if (level == 0 ){
        return itemName
    }
    val levelName = when(level) {
        1 -> ""
        2 -> "Double "
        3 -> "Triple "
        4 -> "Quadruple "
        5 -> "Quintuple "
        else -> "${level}x "
    }
    return "${levelName}Compressed $itemName"
}

fun keyName(level: Int, itemName: String): String {
    val fmtItemName = itemName.lowercase().replace(' ', '_')
    if (level == 0) {
        return fmtItemName
    }
    val levelName = when(level) {
        1 -> ""
        2 -> "_double"
        3 -> "_triple"
        4 -> "_quadruple"
        5 -> "_quintuple"
        else -> "x${level}"
    }
    return "${fmtItemName}_compressed$levelName"
}