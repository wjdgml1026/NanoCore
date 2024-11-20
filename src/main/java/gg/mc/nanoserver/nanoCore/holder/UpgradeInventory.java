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
    private final static HashMap<Material, String> ITEMS = new HashMap<>();
    private final static int[] SUCCESS = {100, 80, 70, 50, 45, 35, 20, 10, 7, 5};
    private final static int[] BROKEN = {0, 5, 7, 10, 15, 20, 35, 50, 60, 80};
    private final static int PREV_ITEM_SLOT = 11;
    private final static int ANVIL_SLOT = 13;
    private final static int NEXT_ITEM_SLOT = 15;

    public final static int[] SWORD_DAMAGE = {0, 1, 2, 3, 5, 7, 8, 10, 12, 15, 20}; // 0 ~ 10

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

        ITEMS.put(Material.WOODEN_SWORD, "나무 검");
        ITEMS.put(Material.LEATHER_HELMET, "가죽 투구");
        ITEMS.put(Material.LEATHER_CHESTPLATE, "가죽 흉갑");
        ITEMS.put(Material.LEATHER_LEGGINGS, "가죽 레깅스");
        ITEMS.put(Material.LEATHER_BOOTS, "가죽 부츠");

        ITEMS.put(Material.STONE_SWORD, "돌 검");
        ITEMS.put(Material.CHAINMAIL_HELMET, "사슬 투구");
        ITEMS.put(Material.CHAINMAIL_CHESTPLATE, "사슬 흉갑");
        ITEMS.put(Material.CHAINMAIL_LEGGINGS, "사슬 레깅스");
        ITEMS.put(Material.CHAINMAIL_BOOTS, "사슬 부츠");

        ITEMS.put(Material.IRON_SWORD, "철 검");
        ITEMS.put(Material.IRON_HELMET, "철 투구");
        ITEMS.put(Material.IRON_CHESTPLATE, "철 흉갑");
        ITEMS.put(Material.IRON_LEGGINGS, "철 레깅스");
        ITEMS.put(Material.IRON_BOOTS, "철 부츠");

        ITEMS.put(Material.GOLDEN_SWORD, "금 검");
        ITEMS.put(Material.GOLDEN_HELMET, "금 투구");
        ITEMS.put(Material.GOLDEN_CHESTPLATE, "금 흉갑");
        ITEMS.put(Material.GOLDEN_LEGGINGS, "금 레깅스");
        ITEMS.put(Material.GOLDEN_BOOTS, "금 부츠");

        ITEMS.put(Material.DIAMOND_SWORD, "다이아몬드 검");
        ITEMS.put(Material.DIAMOND_HELMET, "다이아몬드 투구");
        ITEMS.put(Material.DIAMOND_CHESTPLATE, "다이아몬드 흉갑");
        ITEMS.put(Material.DIAMOND_LEGGINGS, "다이아몬드 레깅스");
        ITEMS.put(Material.DIAMOND_BOOTS, "다이아몬드 부츠");

        ITEMS.put(Material.NETHERITE_SWORD, "네더라이트 검");
        ITEMS.put(Material.NETHERITE_HELMET, "네더라이트 투구");
        ITEMS.put(Material.NETHERITE_CHESTPLATE, "네더라이트 흉갑");
        ITEMS.put(Material.NETHERITE_LEGGINGS, "네더라이트 레깅스");
        ITEMS.put(Material.NETHERITE_BOOTS, "네더라이트 부츠");

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
                if (ITEMS.containsKey(itemStack.getType())) {
                    ItemStack item = inventory.getItem(PREV_ITEM_SLOT);
                    if ( item != null && !PREV_ITEM.isSimilar(item)) {
                        giveItem();
                        init();
                    }
                    setUpgradeItem(itemStack.clone(), e.getSlot());
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

                int n = player.hasPermission("nano.upgrade") ? 0 : RANDOM.nextInt(100);
                if (n < SUCCESS[lv-1]) {
                    // 성공
                    item.setAmount(1);
                    setUpgradeItem(item, slot);
                    if (lv == 0) {
                        giveItem();
                        init();
                        TextComponent message = Component.text()
                                .append(Component.text(player.getName(), NamedTextColor.GOLD))
                                .append(Component.text("님이 ", NamedTextColor.YELLOW))
                                .append(Component.text(ITEMS.getOrDefault(item.getType(), "강화"), NamedTextColor.AQUA))
                                .append(Component.text(" 10강을 달성하였습니다.", NamedTextColor.YELLOW))
                                .build();
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            p.sendMessage(message);
                        }
                        player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
                    } else if (!item.getType().name().endsWith("_SWORD") && lv == 0) {
                        giveItem();
                        init();
                        player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
                    } else {
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.0f);
                    }
                } else {
                    // 실패
                    if (RANDOM.nextInt(100) < BROKEN[lv-1]) {
                        // 파괴
                        player.playSound(player, Sound.BLOCK_ANVIL_DESTROY, 1.2f, 1.0f);
                        init();
                    } else {
                        player.playSound(player, Sound.BLOCK_ANVIL_BREAK, 1.2f, 1.0f);
                    }
                }
            }
        }
    }

    private void setUpgradeItem(ItemStack item, int slot) {
        NBT.get(item, nbt -> {
            lv = nbt.getOrDefault("UpgradeLevel", 0);
        });

        if (lv > 1) {
            item.setAmount(lv);
        }
        inventory.setItem(PREV_ITEM_SLOT, item);

        if ( item.getType().name().endsWith("_SWORD") ) {
            if ( lv >= 10 ){
                lv = 0;
                if (this.slot == -1) {
                    init();
                }
                return;
            }
        } else {
            if ( lv >= 5 ){
                lv = 0;
                if (this.slot == -1) {
                    init();
                }
                return;
            }
        }

        String name;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasDisplayName()) {
            name = itemMeta.getDisplayName();
            if (name.endsWith(" §e+" + lv)) {
                name = name.substring(0, name.length() - 5);
            }
        } else {
            name = ChatColor.AQUA + ITEMS.getOrDefault(item.getType(), "아이템");
        }

        lv++;
        itemMeta.setDisplayName(name + " §e+" + lv);
        ItemStack newItem = item.clone();
        newItem.setItemMeta(itemMeta);
        NBT.modify(newItem, nbt -> {
            nbt.setInteger("UpgradeLevel", lv);
            nbt.modifyMeta((readOnlyNbt, meta) -> {
                meta.setLore(buildLore(newItem.getType(), lv));
            });
        });
        newItem.setAmount(lv);
        inventory.setItem(NEXT_ITEM_SLOT, newItem);

        player.getInventory().clear(slot);
        this.slot = slot;

        if (lv <= 10) {
            ItemMeta meta = anvil.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "--------------------");
            lore.add(ChatColor.AQUA + "성공 확률 " + SUCCESS[lv-1] + "%");
            int fail = 100 - SUCCESS[lv-1];
            int broken = fail * BROKEN[lv-1] / 100;
            lore.add(ChatColor.RED + "실패 확률 " + (fail - broken) + "%");
            lore.add(ChatColor.GRAY + "파괴 확률 " + broken + "%");
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
        if (type.name().endsWith("_SWORD")) {
            lore.add(ChatColor.YELLOW + "★".repeat(Math.min(lv, 10)) + "☆".repeat(Math.max(10-lv, 0)));
            lore.add("");
            lore.add(ChatColor.RED + "+" + SWORD_DAMAGE[lv] + " 공격 피해");
        } else {
            lore.add(ChatColor.YELLOW + "★".repeat(Math.min(lv, 5)) + "☆".repeat(Math.max(5-lv, 0)));
            lore.add("");
            lore.add(ChatColor.RED + String.format("%.1f%% 피해 감소", lv * 2.5));
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
