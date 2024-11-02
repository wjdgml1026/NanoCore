package gg.mc.nanoserver.nanoCore.holder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class TradeInventory implements InventoryHolder {
    public final static ItemStack BORDER, BARRIER, Y, N, TIME1, TIME2, TIME3;
    public final static int[] BORDER_SLOT = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            18, 27, 36, 13, 22, 31, 40, 17, 26, 35, 44,
            45, 46, 48, 49, 50, 52, 53
    };
    public final static List<Integer> PLAYER1_BARRIER = Arrays.asList(39, 38, 37, 30, 29, 28, 21, 20, 19, 12, 11, 10);
    public final static Set<Integer> PLAYER1_SLOT = new HashSet<>(PLAYER1_BARRIER);
    public final static List<Integer> PLAYER2_BARRIER = Arrays.asList(43, 42, 41, 34, 33, 32, 25, 24, 23, 16, 15, 14);
    public final static Set<Integer> PLAYER2_SLOT = new HashSet<>(PLAYER2_BARRIER);

    public final int YN1_SLOT = 47;
    public final int YN2_SLOT = 51;
    public final int TIMER_SLOT = 31;

    public boolean isEnd = false;

    public final Inventory inventory;
    public final Player player1, player2;

    int ticks = 0;

    static {
        ItemMeta itemMeta;

        BORDER = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = BORDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        BORDER.setItemMeta(itemMeta);

        BARRIER = new ItemStack(Material.BARRIER);
        itemMeta = BARRIER.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "상대방 인벤토리 슬롯이 부족합니다.");
        BARRIER.setItemMeta(itemMeta);

        Y = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        itemMeta = Y.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "찬성");
        Y.setItemMeta(itemMeta);

        N = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        itemMeta = N.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "반대");
        N.setItemMeta(itemMeta);

        TIME1 = new ItemStack(Material.YELLOW_DYE);
        itemMeta = TIME1.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "3초 남았습니다.");
        TIME1.setItemMeta(itemMeta);

        TIME2 = new ItemStack(Material.ORANGE_DYE);
        itemMeta = TIME2.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "2초 남았습니다.");
        TIME2.setItemMeta(itemMeta);

        TIME3 = new ItemStack(Material.RED_DYE);
        itemMeta = TIME3.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "1초 남았습니다.");
        TIME3.setItemMeta(itemMeta);
    }

    public TradeInventory(Player player1, Player player2) {
        this.inventory = plugin.getServer().createInventory(this, 54, player1.getName() + " <-> " + player2.getName());
        this.player1 = player1;
        this.player2 = player2;

        for (int i : BORDER_SLOT) {
            inventory.setItem(i, BORDER);
        }

        inventory.setItem(YN1_SLOT, N);
        inventory.setItem(YN2_SLOT, N);

        int count = countEmptySlot(player1.getInventory());
        if (count < 12) {
            for (int i = 0; i < 12 - count; i++) {
                inventory.setItem(PLAYER1_BARRIER.get(i), BARRIER);
            }
        }

        count = countEmptySlot(player1.getInventory());
        if (count < 12) {
            for (int i = 0; i < 12 - count; i++) {
                inventory.setItem(PLAYER2_BARRIER.get(i), BARRIER);
            }
        }
    }

    private int countEmptySlot(Inventory inventory) {
        int emptyCount = 0;
        for (ItemStack item  : inventory.getContents()) {
            if (item == null || item.getType().isEmpty() || item.getType().isAir()) { // 슬롯이 비어 있는지 확인
                emptyCount++;   // 빈 슬롯이면 개수 증가
            }
        }
        return emptyCount;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getInventory().equals(e.getClickedInventory())) return;

        if (PLAYER1_SLOT.contains(e.getSlot())) {
            if (!e.getWhoClicked().equals(player1)) {
                e.setCancelled(true);
            }
            return;
        } else if (PLAYER2_SLOT.contains(e.getSlot())) {
            if (!e.getWhoClicked().equals(player2)) {
                e.setCancelled(true);
            }
            return;
        }
        e.setCancelled(true);

        if (e.getSlot() == YN1_SLOT) {
            if (e.getWhoClicked().equals(player1)) {
                if (N.isSimilar(e.getCurrentItem())) {
                    inventory.setItem(YN1_SLOT, Y);
                    player1.playSound(player1.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.5f, 0.8f);
                    player2.playSound(player1.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.5f, 0.8f);
                    if ( Y.isSimilar(inventory.getItem(YN2_SLOT)) ) {
                        confirmTrade();
                    }
                } else {
                    inventory.setItem(YN1_SLOT, N);
                    player1.playSound(player1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
                    player2.playSound(player1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
                }
            }
        } else if (e.getSlot() == YN2_SLOT) {
            if (e.getWhoClicked().equals(player2)) {
                if (N.isSimilar(e.getCurrentItem())) {
                    inventory.setItem(YN2_SLOT, Y);
                    player1.playSound(player2.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.5f, 0.8f);
                    player2.playSound(player2.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.5f, 0.8f);
                    if (Y.isSimilar(inventory.getItem(YN1_SLOT))) {
                        confirmTrade();
                    }
                } else {
                    inventory.setItem(YN2_SLOT, N);
                    player1.playSound(player2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
                    player2.playSound(player2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
                }
            }
        }
    }

    private void confirmTrade() {
        ticks = 0;
        inventory.setItem(TIMER_SLOT, TIME1);

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (N.isSimilar(inventory.getItem(YN1_SLOT))) {
                inventory.setItem(TIMER_SLOT, BORDER);
                inventory.setItem(YN2_SLOT, N);
                task.cancel();
            }

            if (N.isSimilar(inventory.getItem(YN2_SLOT))) {
                inventory.setItem(TIMER_SLOT, BORDER);
                inventory.setItem(YN1_SLOT, N);
                task.cancel();
            }

            switch (ticks) {
                case 20:
                    inventory.setItem(TIMER_SLOT, TIME2);
                    player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f, 0.8f);
                    player2.playSound(player2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f, 0.8f);
                    break;
                case 40:
                    inventory.setItem(TIMER_SLOT, TIME3);
                    player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f, 0.8f);
                    player2.playSound(player2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.5f, 0.8f);
                    break;
                case 60:
                    successTrade();
                    task.cancel();
                    break;
            }
            ticks++;
        }, 0, 1);
    }

    private void successTrade() {
        isEnd = true;
        player1.playSound(player1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 0.8f);
        player2.playSound(player2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 0.8f);

        player1.sendMessage(ChatColor.YELLOW + "거래가 완료되었습니다!");
        player2.sendMessage(ChatColor.YELLOW + "거래가 완료되었습니다!");

        for (int i : PLAYER1_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player2.getInventory().addItem(item);
            }
        }

        for (int i : PLAYER2_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player1.getInventory().addItem(item);
            }
        }

        player1.getInventory().close();
        player2.getInventory().close();
    }

    public void onClose(InventoryCloseEvent e) {
        if (isEnd) return;
        isEnd = true;

        for (int i : PLAYER1_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player1.getInventory().addItem(item);
            }
        }

        for (int i : PLAYER2_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player2.getInventory().addItem(item);
            }
        }

        if (e.getPlayer().getUniqueId().equals(player1.getUniqueId())) {
            player2.getInventory().close();
            player2.playSound(player2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
            player2.sendMessage(ChatColor.YELLOW + "상대방이 거래를 취소했습니다.");
        } else {
            player1.getInventory().close();
            player1.playSound(player1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
            player1.sendMessage(ChatColor.YELLOW + "상대방이 거래를 취소했습니다.");
        }
    }
}
