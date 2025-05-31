package me.fizzilith.fireSerpent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;


public class FireSerpent extends FireAbility implements AddonAbility {
    private Player p;
    private Location loc;
    private Vector dir;
    private Permission perm;
    private long startTime;
    private boolean Charged;
    private boolean notified;
    private Location start;
    private int particleAmount;
    @Attribute(Attribute.WIDTH)
    private float BallSize;
    static FileConfiguration cm;
    @Attribute(Attribute.DAMAGE)
    private double damage;
    @Attribute(Attribute.COOLDOWN)
    private long cooldown;
    @Attribute(Attribute.CHARGE_DURATION)
    private long chargetime;
    @Attribute(Attribute.RANGE)
    private int range;
    private BendingPlayer bp;
    @Attribute(Attribute.DURATION)
    private long duration;

    static {
        cm = ConfigManager.defaultConfig.get();
    }

    public FireSerpent(Player player) {
        super(player);
        this.damage = cm.getDouble("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Damage");
        this.cooldown = cm.getLong("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Cooldown");
        this.chargetime = cm.getLong("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.ChargeTime");
        this.range = cm.getInt("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Range");
        this.particleAmount = 1;
        this.BallSize = 2.0F;
        this.p = player;
        this.duration = cm.getLong("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Duration");
        this.bp = BendingPlayer.getBendingPlayer(this.p.getName());
        this.loc = GeneralMethods.getTargetedLocation(this.p, 6);
        this.start = GeneralMethods.getTargetedLocation(this.p, 6);
        this.dir = GeneralMethods.getTargetedLocation(this.p, 6).getDirection();
        this.startTime = System.currentTimeMillis();
        this.Charged = false;
        this.notified = false;
        this.start();
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getName() {
        return "me.fizzilith.fireSerpent.FireSerpent";
    }

    public boolean isHarmlessAbility() {
        return false;
    }

    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public void progress() {
        if (!this.Charged) {
            if (this.p.isSneaking() && !this.Charged) {
                this.loc = GeneralMethods.getTargetedLocation(this.p, 6);
                this.start = GeneralMethods.getTargetedLocation(this.p, 6);
                if (this.particleAmount <= 100) {
                    ++this.particleAmount;
                }

                if (this.BallSize >= 0.1F) {
                    this.BallSize = (float)((double)this.BallSize - 0.02);
                }

                if ((new Random()).nextInt(7) == 0) {
                    playFirebendingSound(this.loc);
                }

                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, this.loc, this.particleAmount, this.BallSize, this.BallSize, this.BallSize, 0.0F);
            }

            if (this.p.isSneaking() && System.currentTimeMillis() > this.startTime + this.chargetime) {
                this.particleAmount = 100;
                this.BallSize = 0.2F;
                if ((new Random()).nextInt(7) == 0) {
                    playFirebendingSound(this.loc);
                }

                playFirebendingParticles(this.loc, this.particleAmount, (double)(this.BallSize + 0.05F), (double)(this.BallSize + 0.05F), (double)(this.BallSize + 0.05F));
                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, this.loc, this.particleAmount, (double)(this.BallSize + 0.05F), (double)(this.BallSize + 0.05F), 0.0F);
                if (!this.notified) {
                    playCombustionSound(this.loc);
                    this.p.sendMessage(ChatColor.RED + "Your inner fire is ready to be unleashed into a powerful fire Serpent!");
                    this.notified = true;
                }
            }

            if (!this.p.isSneaking()) {
                if (System.currentTimeMillis() > this.startTime + this.chargetime) {
                    this.loc = this.loc.add(this.dir);
                    this.start = GeneralMethods.getTargetedLocation(this.p, 6);
                    this.dir = GeneralMethods.getTargetedLocation(this.p, 6).getDirection();
                    this.Charged = true;
                    boolean ds = false;
                    ds = true;
                    if (ds && this.Charged) {
                        this.loc.getWorld().playSound(this.loc, Sound.ENTITY_CREEPER_PRIMED, 2.0F, 0.1F);
                        ds = false;
                    }
                } else {
                    this.remove();
                }
            }
        } else {
            if (!this.player.isSneaking()) {
                this.dir = GeneralMethods.getTargetedLocation(this.p, 6).getDirection();
                this.loc.add(this.dir);
            } else {
                this.returnToSender();
            }

            this.loc.add(this.dir);
            this.bp.addCooldown(this);
            if (System.currentTimeMillis() - this.getStartTime() > this.duration) {
                this.remove();
                this.bp.addCooldown(this);
                return;
            }

            if ((new Random()).nextInt(7) == 0) {
                playFirebendingSound(this.loc);
            }

            playFirebendingParticles(this.loc, this.particleAmount, (double)(this.BallSize + 0.05F), (double)(this.BallSize + 0.05F), (double)(this.BallSize + 0.05F));
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, this.loc, this.particleAmount, (double)(this.BallSize + 0.1F), (double)(this.BallSize + 0.1F), 0.0F);

            if (this.p.isDead() || !this.p.isOnline()) {
                return;
            }

            if (GeneralMethods.isSolid(this.loc.getBlock())) {
                this.doExplosion();
                boolean ds = false;
                ds = true;
                if (ds) {
                    this.loc.getWorld().playSound(this.loc, Sound.ENTITY_CREEPER_DEATH, 2.0F, 0.1F);
                    ds = false;
                }

                this.remove();
                return;
            }

            if (this.start.distance(this.loc) > (double)this.range) {
                this.p.sendMessage(ChatColor.RED + "Your Serpent has strayed too far. You have lost control.");
                this.remove();
                return;
            }

            if (this.loc.getBlock().isLiquid()) {
                this.doExplosion();
                this.remove();
                return;
            }

            for(Entity e : GeneralMethods.getEntitiesAroundPoint(this.loc, (double)2.5F)) {
                if (e instanceof LivingEntity && e.getEntityId() != this.p.getEntityId()) {
                    DamageHandler.damageEntity(e, this.damage, this);
                    e.setFireTicks(3);
                }
            }
        }

    }


    public void doExplosion() {
        for(Entity e : GeneralMethods.getEntitiesAroundPoint(this.loc, (double)10.0F)) {
            if (e instanceof LivingEntity && e.getEntityId() != this.p.getEntityId()) {
                if ((new Random()).nextInt(7) == 0) {
                    playFirebendingSound(this.loc);
                }

                playFirebendingParticles(e.getLocation(), 600, (double)0.2F, (double)0.2F, (double)0.2F);
            }
        }

    }


    private void returnToSender() {
        Location loc = this.player.getEyeLocation();
        Vector dV = loc.getDirection().normalize();
        loc.add(new Vector(dV.getX() * (double)6.0F, dV.getY() * (double)6.0F, dV.getZ() * (double)6.0F));
        Vector vector = loc.toVector().subtract(this.loc.toVector());
        this.dir = loc.setDirection(vector).getDirection().normalize();
    }

    @Override
    public String getAuthor() {
        return "Fizzilith";
    }

    @Override
    public String getVersion() {
        return "v1.0.0";
    }

    @Override
    public String getDescription() {
        return String.valueOf(this.getName()) + " " + this.getVersion() + " Developed by " + this.getAuthor() + ": \nAn ancient fire-bending technique, relying on the true flame of real fire sources, allowing you to summon and control a mystical, powerful fire Serpent";
    }


    @Override
    public void load() {
        FileConfiguration c = ConfigManager.defaultConfig.get();
        c.addDefault("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Damage", 6);
        c.addDefault("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Cooldown", 5000);
        c.addDefault("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.ChargeTime", 6000);
        c.addDefault("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Range", 50);
        c.addDefault("ExtraAbilities.Fizzilith.Fire.me.fizzilith.fireSerpent.FireSerpent.Duration", 500000L);
        ConfigManager.defaultConfig.save();
        //ProjectKorra.plugin.getServer().getScheduler().runTaskLater(ProjectKorra.plugin, new (this), 20L);
        ProjectKorra.plugin.getServer().getLogger().info(String.valueOf(this.getName()) + " " + this.getVersion() + " Developed by " + this.getAuthor() + " has been enabled!");
        this.perm = new Permission("bending.ability.fireserpent");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(this.perm);
        this.perm.setDefault(PermissionDefault.TRUE);
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new FireSerpentListener(), ProjectKorra.plugin);
    }

    @Override
    public void stop() {
        ProjectKorra.plugin.getServer().getLogger().info(String.valueOf(this.getName()) + " " + this.getVersion() + "Developed by " + this.getAuthor() + " has been disabled!");
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(this.perm);
        this.remove();
    }
}
