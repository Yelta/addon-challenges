package bentobox.addon.challenges;

import org.bukkit.Bukkit;

import bentobox.addon.challenges.commands.ChallengesCommand;
import bentobox.addon.challenges.commands.admin.Challenges;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.configuration.Config;

/**
 * Add-on to BSkyBlock that enables challenges
 * @author tastybento
 *
 */
public class ChallengesAddon extends Addon {

    private ChallengesManager challengesManager;
    private String permissionPrefix = "addon";
    private FreshSqueezedChallenges importManager;

    @Override
    public void onLoad() {
        // Save default config.yml
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Check if it is enabled - it might be loaded, but not enabled.
        if (getPlugin() == null || !getPlugin().isEnabled()) {
            Bukkit.getLogger().severe("BentoBox is not available or disabled!");
            this.setEnabled(false);
            return;
        }

        // Challenges Manager
        challengesManager = new ChallengesManager(this);
        // Challenge import setup
        importManager = new FreshSqueezedChallenges(this);

        // Register commands - run one tick later to allow all addons to load
        // AcidIsland hook in
        getServer().getScheduler().runTask(getPlugin(), () -> {
            this.getPlugin().getAddonsManager().getAddonByName("AcidIsland").ifPresent(a -> {
                CompositeCommand acidIslandCmd = getPlugin().getCommandsManager().getCommand("ai");
                if (acidIslandCmd != null) {
                    new ChallengesCommand(this, acidIslandCmd);
                    CompositeCommand acidCmd = getPlugin().getCommandsManager().getCommand("acid");
                    new Challenges(this, acidCmd);
                    
                }
            });
            this.getPlugin().getAddonsManager().getAddonByName("BSkyBlock").ifPresent(a -> {
                // BSkyBlock hook in
                CompositeCommand bsbIslandCmd = getPlugin().getCommandsManager().getCommand("island");
                if (bsbIslandCmd != null) {
                    new ChallengesCommand(this, bsbIslandCmd);
                    CompositeCommand bsbAdminCmd = getPlugin().getCommandsManager().getCommand("bsbadmin");
                    new Challenges(this, bsbAdminCmd);
                }
            });
        });

        // Done
    }

    @Override
    public void onDisable(){
        if (challengesManager != null) {
            challengesManager.save(false);
        }
    }

    public ChallengesManager getChallengesManager() {
        return challengesManager;
    }

    public String getPermissionPrefix() {
        return permissionPrefix ;
    }

    /**
     * @return the importManager
     */
    public FreshSqueezedChallenges getImportManager() {
        return importManager;
    }

}
