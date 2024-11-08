package gg.mc.nanoserver.nanoCore.holder;

import de.tr7zw.changeme.nbtapi.NBT;
import gg.mc.nanoserver.nanoCore.interfaces.CustomInventory;
import gg.mc.nanoserver.nanoCore.util.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class UpgradeInventory implements CustomInventory {
    public static ItemStack MATERIAL;
    private final static Random RANDOM = new Random();
    private final static ItemStack PREV_ITEM, NEXT_ITEM;
    private final static ItemStack YELLOW, LIME, WHITE;
    private static ItemStack anvil;
    private final static HashMap<Material, String> ITEM_NAME = new HashMap<>();
    private final static int[] SUCCESS = {100, 90, 80, 70, 50, 40, 30, 20, 10, 5};
    private final static int[] BROKEN = {0, 0, 2, 6, 15, 24, 28, 40, 54, 66};
    private final static int PREV_ITEM_SLOT = 11;
    private final static int ANVIL_SLOT = 13;
    private final static int NEXT_ITEM_SLOT = 15;

    private final Inventory inventory;
    private final Player player;
    private int slot = -1;
    private int lv = 0;

    static {
        ItemMeta itemMeta;

        PREV_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = PREV_ITEM.getItemMeta();
        itemMeta.setDisplayName("장비");
        PREV_ITEM.setItemMeta(itemMeta);

        NEXT_ITEM = new ItemStack(Material.IRON_NUGGET);
        itemMeta = NEXT_ITEM.getItemMeta();
        itemMeta.setDisplayName("결과");
        NEXT_ITEM.setItemMeta(itemMeta);

//        MATERIAL = new ItemStack(Material.PAPER);
//        itemMeta = MATERIAL.getItemMeta();
//        itemMeta.setDisplayName(ChatColor.AQUA + "강화석");
//        itemMeta.setCustomModelData(100);
//        MATERIAL.setItemMeta(itemMeta);

        ITEM_NAME.put(Material.NETHERITE_SWORD, "네더라이트 검");
        ITEM_NAME.put(Material.NETHERITE_HELMET, "네더라이트 투구");
        ITEM_NAME.put(Material.NETHERITE_CHESTPLATE, "네더라이트 흉갑");
        ITEM_NAME.put(Material.NETHERITE_LEGGINGS, "네더라이트 레깅스");
        ITEM_NAME.put(Material.NETHERITE_BOOTS, "네더라이트 부츠");
        ITEM_NAME.put(Material.BOW, "활");

        YELLOW = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = YELLOW.getItemMeta();
        itemMeta.setDisplayName(" ");
        YELLOW.setItemMeta(itemMeta);

        LIME = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        itemMeta = LIME.getItemMeta();
        itemMeta.setDisplayName(" ");
        LIME.setItemMeta(itemMeta);

        WHITE = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        itemMeta = WHITE.getItemMeta();
        itemMeta.setDisplayName(" ");
        WHITE.setItemMeta(itemMeta);
    }

    public UpgradeInventory(Player player) {
        this.inventory = plugin.getServer().createInventory(this, 27, "강화");
        this.player = player;
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, YELLOW);
            inventory.setItem(i+18, YELLOW);
        }
        inventory.setItem(9, YELLOW);
        inventory.setItem(10, LIME);
        inventory.setItem(12, WHITE);
        inventory.setItem(17, YELLOW);
        inventory.setItem(16, LIME);
        inventory.setItem(14, WHITE);
        init();
    }

    public void init() {
        this.lv = 0;
        this.slot = -1;

        anvil = new ItemStack(Material.ANVIL);
        ItemMeta itemMeta = anvil.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "강화");
        anvil.setItemMeta(itemMeta);

        inventory.setItem(ANVIL_SLOT, anvil);
        inventory.setItem(PREV_ITEM_SLOT, PREV_ITEM);
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
                switch (itemStack.getType()) {
                    case NETHERITE_SWORD:
                    case NETHERITE_HELMET:
                    case NETHERITE_CHESTPLATE:
                    case NETHERITE_LEGGINGS:
                    case NETHERITE_BOOTS:
                    case BOW:
                        ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
                        if ( item != null && !PREV_ITEM.isSimilar(item)) {
                            giveItem();
                            init();
                        }
                        setUpgradeItem(itemStack, e.getSlot());
                        break;
                }
            }
        } else {
            if (e.getSlot() == PREV_ITEM_SLOT) {
                ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
                if ( item != null && !PREV_ITEM.isSimilar(item)) {
                    giveItem();
                }
                init();
            } else if (e.getSlot() == ANVIL_SLOT) {
                ItemStack item = inventory.getItem(NEXT_ITEM_SLOT);
                if ( NEXT_ITEM.isSimilar(item) || item == null) {
                    return;
                }
                if (!InventoryUtils.hasItem(player.getInventory(), MATERIAL)) {
                    player.playSound(player, Sound.BLOCK_DISPENSER_FAIL, 1.5f, 1.0f);
                    return;
                }
                InventoryUtils.removeItems(player.getInventory(), MATERIAL);

                int success = lv < 10 ? SUCCESS[lv-1] : 1;
                int broken = lv < 10 ? BROKEN[lv-1] : 70;
                int n = player.hasPermission("nano.upgrade") ? 0 : RANDOM.nextInt(100);

                if (n < success) {
                    item.setAmount(1);
                    setUpgradeItem(item, slot);
                    if (lv > 10) {
                        giveItem();
                        init();

                        TextComponent message = Component.text()
                                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                                .append(Component.text("님이 ", NamedTextColor.YELLOW))
                                .append(Component.text(ITEM_NAME.getOrDefault(item.getType(), "강화"), NamedTextColor.AQUA))
                                .append(Component.text(" 10강을 달성하였습니다.", NamedTextColor.YELLOW))
                                .build();
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            p.sendMessage(message);
                            p.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
                        }
                    } else {
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.0f);
                    }
                } else if (n < 100 - broken) {
                    player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2f, 1.0f);
                } else {
                    player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1.2f, 1.0f);
                    init();
                }
            }
        }
    }

    private void setUpgradeItem(ItemStack item, int slot) {
        NBT.get(item, nbt -> {
            lv = nbt.getOrDefault("UpgradeLevel", 0);
        });

        if (lv > 9 && this.slot == -1) {
            lv = 0;
            return;
        } else if (lv > 1) {
            item.setAmount(lv);
        }

        lv++;
        ItemStack newItem = item.clone();
        NBT.modify(newItem, nbt -> {
            nbt.setInteger("UpgradeLevel", lv);
            nbt.modifyMeta((readOnlyNbt, meta) -> {
                meta.setLore(buildLore(newItem.getType(), lv));
            });
        });
        newItem.setAmount(lv);

        inventory.setItem(PREV_ITEM_SLOT, item);
        inventory.setItem(NEXT_ITEM_SLOT, newItem);

        player.getInventory().clear(slot);
        this.slot = slot;

        if (lv < 10) {
            ItemMeta meta = anvil.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "--------------------");
            lore.add(ChatColor.AQUA + "성공 확률 " + SUCCESS[lv-1] + "%");
            lore.add(ChatColor.RED + "실패 확률 " + (100 - SUCCESS[lv-1] - BROKEN[lv-1]) + "%");
            lore.add(ChatColor.GRAY + "파괴 확률 " + BROKEN[lv-1] + "%");
            lore.add(ChatColor.GRAY + "--------------------");
            if (lv > 1) {
                lore.addAll(buildLore(item.getType(), lv-1));
            } else {
                lore.add("");
                lore.add(ChatColor.YELLOW + "강화 Lv 0");
                lore.add(ChatColor.YELLOW + "☆☆☆☆☆☆☆☆☆☆");
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "--------------------");
            lore.addAll(buildLore(item.getType(), lv));
            lore.add("");
            lore.add(ChatColor.GRAY + "--------------------");
            meta.setLore(lore);
            anvil.setItemMeta(meta);
            inventory.setItem(ANVIL_SLOT, anvil);
        }
    }

    private @NotNull List<String> buildLore(Material type, int lv) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "강화 Lv " + lv);
        lore.add(ChatColor.YELLOW + "★".repeat(Math.min(lv, 10)) + "☆".repeat(Math.max(10-lv, 0)));
        lore.add("");
        switch (type) {
            case NETHERITE_SWORD:
            case BOW:
                lore.add(ChatColor.RED + "+" + lv + " 공격 피해");
                break;
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                lore.add(ChatColor.RED + "+" + lv + " 피해 감소");
                break;
        }
        return lore;
    }

    private void giveItem() {
        ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
        if ( item != null && !PREV_ITEM.isSimilar(item) ) {
            item.setAmount(1);
            ItemStack tmp = player.getInventory().getItem(slot);
            if (tmp == null  || tmp.isEmpty()) {
                player.getInventory().setItem(slot, item);
            } else {
                int i = player.getInventory().firstEmpty();
                if (i == -1) {
                    ItemStack it = player.getInventory().getItem(slot);
                    player.getInventory().setItem(slot, item);
                    if (it != null) {
                        player.getWorld().dropItem(player.getLocation(), it);
                    }
                } else {
                    player.getInventory().setItem(i, item);
                }
            }
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent e) {
        giveItem();
    }
}
