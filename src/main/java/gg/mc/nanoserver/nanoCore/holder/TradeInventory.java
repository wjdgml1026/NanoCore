package gg.mc.nanoserver.nanoCore.holder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static gg.mc.nanoserver.nanoCore.NanoCore.plugin;

public class TradeInventory implements InventoryHolder {
    private final static ItemStack BORDER, EMPTY, Y, N, TIME1, TIME2, TIME3;
    private final static int[] BORDER_SLOT = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            18, 27, 36, 13, 22, 31, 40, 17, 26, 35, 44,
            45, 46, 48, 49, 50, 52, 53
    };

    private final static Set<Integer> MY_SLOT = new HashSet<>(Arrays.asList(39, 38, 37, 30, 29, 28, 21, 20, 19, 12, 11, 10));
    private final static Set<Integer> OTHER_SLOT = new HashSet<>(Arrays.asList(43, 42, 41, 34, 33, 32, 25, 24, 23, 16, 15, 14));

    private final int YN1_SLOT = 47;
    private final int YN2_SLOT = 51;
    private final int TIMER_SLOT = 31;

    private boolean isEnd = false;

    private TradeInventory otherHolder;
    private final Inventory inventory;
    private final Player player;

    private int ticks = 0;

    static {
        ItemMeta itemMeta;

        BORDER = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        itemMeta = BORDER.getItemMeta();
        itemMeta.setDisplayName(" ");
        BORDER.setItemMeta(itemMeta);

        EMPTY = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        itemMeta = EMPTY.getItemMeta();
        itemMeta.setDisplayName(" ");
        EMPTY.setItemMeta(itemMeta);

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

    public TradeInventory(Player player, Player other) {
        this.inventory = plugin.getServer().createInventory(this, 54, player.getName() + " <-> " + other.getName());
        this.player = player;

        for (int i : BORDER_SLOT) {
            inventory.setItem(i, BORDER);
        }
        inventory.setItem(YN1_SLOT, N);
        inventory.setItem(YN2_SLOT, N);
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

    public void setOtherHolder(TradeInventory otherHolder) {
        this.otherHolder = otherHolder;
    }

    public void playSound(Sound sound) {
        player.playSound(player.getLocation(), sound, 1.5f, 0.8f);
        otherHolder.getPlayer().playSound(player.getLocation(), sound, 1.5f, 0.8f);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void onDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getInventory().equals(e.getClickedInventory()))
            return;
        if (MY_SLOT.contains(e.getSlot())) {
            if (!BORDER.isSimilar(inventory.getItem(TIMER_SLOT))) {
                inventory.setItem(TIMER_SLOT, BORDER);
                otherHolder.getInventory().setItem(TIMER_SLOT, BORDER);

                inventory.setItem(YN1_SLOT, N);
                inventory.setItem(YN2_SLOT, N);
                otherHolder.getInventory().setItem(YN1_SLOT, N);
                otherHolder.getInventory().setItem(YN2_SLOT, N);

                playSound(Sound.ENTITY_VILLAGER_NO);
            }
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 2f, 0.8f);
            final int slot = e.getSlot();
            plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                otherHolder.getInventory().setItem(slot + 4, inventory.getItem(slot));
                otherHolder.getPlayer().playSound(otherHolder.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 2f, 0.8f);
                task.cancel();
            }, 1, 1);
            return;
        }
        
        e.setCancelled(true);

        if (e.getSlot() == YN1_SLOT) {
            if (N.isSimilar(e.getCurrentItem())) {
                inventory.setItem(YN1_SLOT, Y);
                otherHolder.getInventory().setItem(YN2_SLOT, Y);

                playSound(Sound.ENTITY_VILLAGER_YES);

                if ( Y.isSimilar(otherHolder.getInventory().getItem(YN1_SLOT)) ) {
                    confirmTrade();
                }
            } else {
                inventory.setItem(YN1_SLOT, N);
                otherHolder.getInventory().setItem(YN2_SLOT, N);

                playSound(Sound.ENTITY_VILLAGER_NO);
            }
        }
    }

    private void confirmTrade() {
        ticks = 0;
        inventory.setItem(TIMER_SLOT, TIME1);
        otherHolder.getInventory().setItem(TIMER_SLOT, TIME1);

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (N.isSimilar(inventory.getItem(YN1_SLOT))) {
                inventory.setItem(TIMER_SLOT, BORDER);
                otherHolder.getInventory().setItem(TIMER_SLOT, BORDER);
                inventory.setItem(YN1_SLOT, N);
                otherHolder.getInventory().setItem(YN2_SLOT, N);
                task.cancel();
            }

            if (N.isSimilar(otherHolder.getInventory().getItem(YN1_SLOT))) {
                inventory.setItem(TIMER_SLOT, BORDER);
                otherHolder.getInventory().setItem(TIMER_SLOT, BORDER);
                inventory.setItem(YN2_SLOT, N);
                otherHolder.getInventory().setItem(YN1_SLOT, N);
                task.cancel();
            }

            switch (ticks) {
                case 20:
                    inventory.setItem(TIMER_SLOT, TIME2);
                    otherHolder.getInventory().setItem(TIMER_SLOT, TIME2);
                    playSound(Sound.BLOCK_NOTE_BLOCK_BELL);
                    break;
                case 40:
                    inventory.setItem(TIMER_SLOT, TIME3);
                    otherHolder.getInventory().setItem(TIMER_SLOT, TIME3);
                    playSound(Sound.BLOCK_NOTE_BLOCK_BELL);
                    break;
                case 60:
                    success();
                    otherHolder.success();
                    task.cancel();
                    break;
            }
            ticks++;
        }, 0, 1);
    }

    private void success() {
        isEnd = true;
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 0.8f);
        player.sendMessage(ChatColor.YELLOW + "거래가 완료되었습니다!");
        for (int i : OTHER_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player.getInventory().addItem(item);
            }
        }
        player.getInventory().close();
    }

    public void onClose(InventoryCloseEvent e) {
        if (isEnd) return;
        isEnd = true;

        for (int i : MY_SLOT) {
            ItemStack item = inventory.getItem(i);
            if (!(item == null || item.getType().isAir())) {
                player.getInventory().addItem(item);
            }
        }

        if (!otherHolder.isEnd()) {
            otherHolder.getInventory().close();
            otherHolder.getPlayer().playSound(otherHolder.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5f, 0.8f);
            otherHolder.getPlayer().sendMessage(ChatColor.YELLOW + "상대방이 거래를 취소했습니다.");
        }
    }
}
