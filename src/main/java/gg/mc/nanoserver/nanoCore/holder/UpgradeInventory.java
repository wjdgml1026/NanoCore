package gg.mc.nanoserver.nanoCore.holder;

import gg.mc.nanoserver.nanoCore.interfaces.CustomInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class UpgradeInventory implements CustomInventory {
    private final static Random RANDOM = new Random();
    private final static ItemStack PREV_INFO, PREV_ITEM, ANVIL, NEXT_INFO, NEXT_ITEM;
    private final static int[] PERCENT = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10};
    private final static int PREV_INFO_SLOT = 2;
    private final static int PREV_ITEM_SLOT = 11;
    private final static int ANVIL_SLOT = 13;
    private final static int NEXT_INFO_SLOT = 6;
    private final static int NEXT_ITEM_SLOT = 15;

    private final Inventory inventory;
    private final Player player;
    private int slot = -1;
    private int lv = 0;

    static {
        ItemMeta itemMeta;

        PREV_INFO = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = PREV_INFO.getItemMeta();
        itemMeta.setDisplayName("장비");
        PREV_INFO.setItemMeta(itemMeta);

        PREV_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = PREV_ITEM.getItemMeta();
        itemMeta.setDisplayName("장비");
        PREV_ITEM.setItemMeta(itemMeta);

        ANVIL = new ItemStack(Material.ANVIL);
        itemMeta = ANVIL.getItemMeta();
        itemMeta.setDisplayName("강화");
        ANVIL.setItemMeta(itemMeta);

        NEXT_INFO = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = NEXT_INFO.getItemMeta();
        itemMeta.setDisplayName("결과");
        NEXT_INFO.setItemMeta(itemMeta);

        NEXT_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = NEXT_ITEM.getItemMeta();
        itemMeta.setDisplayName("결과");
        NEXT_ITEM.setItemMeta(itemMeta);
    }

    public UpgradeInventory(Player player) {
        this.inventory = plugin.getServer().createInventory(this, 36, "강화");
        this.player = player;

        inventory.setItem(PREV_INFO_SLOT, PREV_INFO);
        inventory.setItem(PREV_ITEM_SLOT, PREV_ITEM);
        inventory.setItem(ANVIL_SLOT, ANVIL);
        inventory.setItem(NEXT_INFO_SLOT, NEXT_INFO);
        inventory.setItem(NEXT_ITEM_SLOT, NEXT_ITEM);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if (player.getInventory().equals(e.getClickedInventory())) {
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack != null) {
                setUpgradeItem(itemStack, e.getSlot());
            }
        } else {
            if (e.getSlot() == PREV_ITEM_SLOT) {
                ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
                if ( !PREV_ITEM.isSimilar(item) && item != null) {
                    lv = 0;
                    slot = -1;
                    giveItem();
                }
                slot = -1;
                inventory.setItem(PREV_INFO_SLOT, PREV_INFO);
                inventory.setItem(PREV_ITEM_SLOT, PREV_ITEM);
                inventory.setItem(NEXT_INFO_SLOT, NEXT_INFO);
                inventory.setItem(NEXT_ITEM_SLOT, NEXT_ITEM);
            } else if (e.getSlot() == ANVIL_SLOT) {
                ItemStack item = inventory.getItem(NEXT_ITEM_SLOT);
                if ( !NEXT_ITEM.isSimilar(item) && item != null) {
                    int percent = lv < 10 ? PERCENT[lv] : 10;
                    if (RANDOM.nextInt(100) < percent) {
                        item.setAmount(1);
                        setUpgradeItem(item, slot);
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.0f);
                    } else {
                        player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2f, 1.0f);
                    }
                }
            }
        }
    }

    private void setUpgradeItem(ItemStack prevItem, int slot) {
        String name = switch (prevItem.getType()) {
            case WOODEN_SWORD -> "나무 검";
            case STONE_SWORD -> "돌 검";
            case IRON_SWORD -> "철 검";
            case GOLDEN_SWORD -> "금 검";
            case DIAMOND_SWORD -> "다이아몬드 검";
            case NETHERITE_SWORD -> "네더라이트 검";
            default -> null;
        };
        if (name == null) {
            return;
        }
        this.slot = slot;
        inventory.clear(slot);

        ItemStack nextItem = prevItem.clone();
        ItemMeta meta = nextItem.getItemMeta();

        lv = 0;
        if (meta.hasDisplayName()) {
            if (meta.getDisplayName().contains("+")) {
                String[] tmp = meta.getDisplayName().split("\\+");
                try {
                    lv = Integer.parseInt(tmp[tmp.length - 1]);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning(e.getMessage());
                }
            }
        }
        meta.setDisplayName(ChatColor.WHITE + name + " +" + lv);
        AttributeModifier damageModifier = new AttributeModifier(
                UUID.randomUUID(),
                "generic.attackDamage", 1,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
        nextItem.setItemMeta(meta);

        prevItem.setAmount(lv);
        nextItem.setAmount(lv+1);

        inventory.setItem(PREV_ITEM_SLOT, prevItem);
        inventory.setItem(NEXT_ITEM_SLOT, nextItem);
    }

    private void giveItem() {
        ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
        if ( !PREV_ITEM.isSimilar(item) && item != null) {
            item.setAmount(1);

            ItemStack tmp = player.getInventory().getItem(slot);
            if (tmp == null  || tmp.isEmpty()) {
                player.getInventory().setItem(slot, item);
            } else {
                slot = player.getInventory().firstEmpty();
                if (slot == -1) {
                    player.getWorld().dropItem(player.getLocation(), item);
                } else {
                    player.getInventory().setItem(slot, item);
                }
            }
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {
        giveItem();
    }
}
