package me.developful.village;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Village extends JavaPlugin implements Listener {
    public static final HashMap<String, PlayerVillager> VILLAGERS = new HashMap<>();
    private final File villagersData = new File(getDataFolder() + "/", "villagers.data");
    private final File jobData = new File(getDataFolder() + "/", "jobs.yml");

    @Override
    public void onEnable() {
        try {
            villagersData.createNewFile();
            jobData.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readVillagerData();
        readJobData();

        var commands = new Job("NONE");
        var manager = getServer().getPluginManager();

        manager.registerEvents(new PlayerVillager(null), this);
        getCommand("job").setTabCompleter(commands);
        getCommand("job").setExecutor(commands);
    }

    @Override
    public void onDisable() {
        dumpVillagerData();
        dumpJobData();
    }

    private String readCharsUntilDelimiter(FileInputStream inputStream, char delimiter) throws IOException {
        int num;
        StringBuilder username = new StringBuilder();
        while ((num = inputStream.read()) != delimiter) {
            // we read all data possible
            if (num == -1) {
                inputStream.close();
                return "";
            }

            username.append((char) num);
        }

        return username.toString();
    }

    private void readJobData() {
        try {
            var inputStream = new FileInputStream(jobData.getAbsolutePath());

            while (inputStream.available() > 0) {
                var job = readCharsUntilDelimiter(inputStream, ':');
                if (job.isEmpty())
                    break;
                var material = readCharsUntilDelimiter(inputStream, '\n').trim();

                Job.JOB_MATERIALS.put(job.toUpperCase(), Material.valueOf(material.toUpperCase()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpJobData() {
        try {
            var outputStream = new FileOutputStream(jobData.getAbsolutePath());

            for (Map.Entry<String, Material> entry : Job.JOB_MATERIALS.entrySet()) {
                var job = entry.getKey();
                var material = entry.getValue();

                outputStream.write(job.getBytes());
                outputStream.write(':');
                outputStream.write(' ');
                outputStream.write(material.name().getBytes());
                outputStream.write('\n');
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readVillagerData() {
        try {
            var inputStream = new FileInputStream(villagersData.getAbsolutePath());
            while (inputStream.available() > 0) {
                var user = readCharsUntilDelimiter(inputStream, ':');
                if (user.isEmpty())
                    break;

                var role = readCharsUntilDelimiter(inputStream, ':');
                var stringlevel = readCharsUntilDelimiter(inputStream, ':');
                var level = Float.parseFloat(stringlevel);

                var stringbalance = readCharsUntilDelimiter(inputStream, '\\');
                var balance = Double.parseDouble(stringbalance);

                var job = new Job(role.toUpperCase());
                var villager = new PlayerVillager(user, balance, job);
                villager.job.level = level;

                VILLAGERS.put(user, villager);
            }

            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpVillagerData() {
        try {
            var outputStream = new FileOutputStream(villagersData.getAbsolutePath());

            for (Map.Entry<String, PlayerVillager> entry : VILLAGERS.entrySet()) {
                var villager = entry.getValue();
                var user = entry.getKey();

                var level = Float.toString(villager.job.level);
                var balance = Double.toString(villager.balance);

                outputStream.write(user.getBytes());
                outputStream.write(':');

                outputStream.write(villager.job.type.getBytes());
                outputStream.write(':');

                outputStream.write(level.getBytes());
                outputStream.write(':');

                outputStream.write(balance.getBytes());
                outputStream.write('\\');
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
