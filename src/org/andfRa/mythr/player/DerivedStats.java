package org.andfRa.mythr.player;

import java.util.Random;

import org.andfRa.mythr.config.AttributeConfiguration;
import org.andfRa.mythr.config.CreatureConfiguration;
import org.andfRa.mythr.config.SkillConfiguration;
import org.andfRa.mythr.config.VanillaConfiguration;
import org.andfRa.mythr.items.ItemType;
import org.andfRa.mythr.items.MythrItem;
import org.andfRa.mythr.util.LinearFunction;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public abstract class DerivedStats {


	/** Random number generator. */
	public static Random RANDOM = new Random();
	
	/** Derived stats for default creatures. */
	public static DerivedStats DEFAULT_CREATURE_STATS = new DerivedStats() {
		
		@Override
		protected int getAttributeScore(String attribName) {
			// TODO Auto-generated method stub
			return CreatureConfiguration.getDefaultAttribScore();
		}

		@Override
		protected int getSkillScore(String skillName) {
			return CreatureConfiguration.getDefaultSkillScore();
		}
		
	};
	
	
	/** Minimum base damage. */
	private int minBaseDmg = 0;

	/** Maximum base damage. */
	private int maxBaseDmg = 0;


	/** Melee damage modifier. */
	private int meleeDmgMod = 0;

	/** Ranged damage modifier. */
	private int rangedDmgMod = 0;

	/** Magic damage modifier. */
	private int magicDmgMod = 0;

	/** Curse damage modifier. */
	private int curseDmgMod = 0;

	/** Blessing damage modifier. */
	private int blessingDmgMod = 0;

	
	/** Melee attack rating. */
	private int meleeAR = 1;

	/** Ranged attack rating. */
	private int rangedAR = 1;

	/** Magic attack rating. */
	private int magicAR = 1;

	/** Curse attack rating. */
	private int curseAR = 1;

	/** Blessing attack rating. */
	private int blessingAR = 1;


	/** Armour induced damage multiplier. */
	private double armour = 1.0;

	
	/** Light armour defence rating. */
	private int lightDR = 0;

	/** Heavy armour defence rating. */
	private int heavyDR = 0;

	/** Exotic armour defence rating. */
	private int exoticDR = 0;

	
	// CONSTRUCTION:
	/**
	 * Calculates the derived stats.
	 * 
	 * @param living living entity
	 */
	public DerivedStats() {
		
	}

	/**
	 * Update all weapon stats.
	 * 
	 * @param living attacker living entity
	 */
	public void updateWeapon(LivingEntity living)
	 {
		resetWeapon();
		
		MythrItem mitem;
		EntityEquipment inventory = living.getEquipment();
		
		// Weapon item:
		if(inventory.getItemInHand() != null){
			
			mitem = MythrItem.fromBukkitItem(inventory.getItemInHand());
			
			switch (mitem.getType()) {
			case MELEE_WEAPON:
				meleeAR+= mitem.getAttackRating();
				break;
			
			case RANGED_WEAPON:
				rangedAR+= mitem.getAttackRating();
				break;
			
			case MAGIC_WEAPON:
				magicAR+= mitem.getAttackRating();
				break;
			
			case CURSE_WEAPON:
				curseAR+= mitem.getAttackRating();
				break;
				
			case BLESSING_WEAPON:
				blessingAR+= mitem.getAttackRating();
				break;

			default:
				break;
			}
			
			minBaseDmg = mitem.getDmgMin();
			maxBaseDmg = mitem.getDmgMax();
			
		}
		// No weapon item:
		else{
			minBaseDmg = VanillaConfiguration.DEFAULT_DAMAGE;
			maxBaseDmg = VanillaConfiguration.DEFAULT_DAMAGE;
		}

		// Skills:
		Skill[] skills = SkillConfiguration.getSkills();
		for (int i = 0; i < skills.length; i++) {
			int score = getSkillScore(skills[i].getName());
			
			meleeAR+= skills[i].getSpecifier(Specifier.MELEE_ATTACK_RATING_MODIFIER, score);
			rangedAR+= skills[i].getSpecifier(Specifier.RANGED_ATTACK_RATING_MODIFIER, score);
			magicAR+= skills[i].getSpecifier(Specifier.MAGIC_ATTACK_RATING_MODIFIER, score);
			curseAR+= skills[i].getSpecifier(Specifier.CURSE_ATTACK_RATING_MODIFIER, score);
			blessingAR+= skills[i].getSpecifier(Specifier.BLESSING_ATTACK_RATING_MODIFIER, score);
		}

		// Skills:
		Attribute[] attributes = AttributeConfiguration.getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			int score = getAttributeScore(attributes[i].getName());
			
			meleeDmgMod+= attributes[i].getSpecifier(Specifier.MELEE_ATTACK_DAMAGE_MODIFIER, score);
			rangedDmgMod+= attributes[i].getSpecifier(Specifier.RANGED_ATTACK_DAMAGE_MODIFIER, score);
			magicDmgMod+= attributes[i].getSpecifier(Specifier.MAGIC_ATTACK_DAMAGE_MODIFIER, score);
			curseDmgMod+= attributes[i].getSpecifier(Specifier.CURSE_ATTACK_DAMAGE_MODIFIER, score);
			blessingDmgMod+= attributes[i].getSpecifier(Specifier.BLESSING_ATTACK_DAMAGE_MODIFIER, score);
		}
		
		
	 }

	/** Resets weapon stats. */
	public void resetWeapon()
	 {
		minBaseDmg = 0;
		maxBaseDmg = 0;
		
		meleeAR = 1;
		rangedAR = 1;
		magicAR = 1;
		curseAR = 1;
		blessingAR = 1;
	 }

	/**
	 * Updates all armour stats.
	 * 
	 * @param living living entity
	 */
	public void updateArmour(LivingEntity living)
	 {
		resetArmour();

		Material material;
		MythrItem mitem;
		EntityEquipment inventory = living.getEquipment();
		
		double light = 0.0;
		double heavy = 0.0;
		double exotic = 0.0;
		
		// Helmet:
		if(inventory.getHelmet() != null){
			mitem = MythrItem.fromBukkitItem(inventory.getHelmet());
			material = mitem.getMaterial();
			
			switch (mitem.getType()) {
			case LIGHT_ARMOUR:
				lightDR+= mitem.getDefenceRating();
				light+= VanillaConfiguration.HELMET_RATIO;
				break;
			
			case HEAVY_ARMOUR:
				heavyDR+= mitem.getDefenceRating();
				heavy+= VanillaConfiguration.HELMET_RATIO;
				break;

			case EXOTIC_ARMOUR:
				exoticDR+= mitem.getDefenceRating();
				exotic+= VanillaConfiguration.HELMET_RATIO;
				break;
				
			default:
				break;
			}
			
			switch (material) {
			case LEATHER_HELMET:
				armour+= VanillaConfiguration.LEATHER_HELMET_MULTIPLIER;
				break;
			
			case GOLD_HELMET:
				armour+= VanillaConfiguration.GOLD_HELMET_MULTIPLIER;
				break;

			case CHAINMAIL_HELMET:
				armour+= VanillaConfiguration.CHAINMAIL_HELMET_MULTIPLIER;
				break;

			case IRON_HELMET:
				armour+= VanillaConfiguration.IRON_HELMET_MULTIPLIER;
				break;
			
			case DIAMOND_HELMET:
				armour+= VanillaConfiguration.DIAMOND_HELMET_MULTIPLIER;
				break;
				
			default:
				break;
			}
		}
		
		// Chestplate:
		if(inventory.getChestplate() != null){
			mitem = MythrItem.fromBukkitItem(inventory.getChestplate());
			material = mitem.getMaterial();

			switch (mitem.getType()) {
			case LIGHT_ARMOUR:
				lightDR+= mitem.getDefenceRating();
				light+= VanillaConfiguration.HELMET_RATIO;
				break;
			
			case HEAVY_ARMOUR:
				heavyDR+= mitem.getDefenceRating();
				heavy+= VanillaConfiguration.HELMET_RATIO;
				break;

			case EXOTIC_ARMOUR:
				exoticDR+= mitem.getDefenceRating();
				exotic+= VanillaConfiguration.HELMET_RATIO;
				break;
				
			default:
				break;
			}
			
			switch (material) {
			case LEATHER_CHESTPLATE:
				armour+= VanillaConfiguration.LEATHER_CHESTPLATE_MULTIPLIER;
				break;
			
			case GOLD_CHESTPLATE:
				armour+= VanillaConfiguration.GOLD_CHESTPLATE_MULTIPLIER;
				break;

			case CHAINMAIL_CHESTPLATE:
				armour+= VanillaConfiguration.CHAINMAIL_CHESTPLATE_MULTIPLIER;
				break;

			case IRON_CHESTPLATE:
				armour+= VanillaConfiguration.IRON_CHESTPLATE_MULTIPLIER;
				break;
			
			case DIAMOND_CHESTPLATE:
				armour+= VanillaConfiguration.DIAMOND_CHESTPLATE_MULTIPLIER;
				break;
				
			default:
				break;
			}
		}
		
		// Leggings:
		if(inventory.getLeggings() != null){
			mitem = MythrItem.fromBukkitItem(inventory.getLeggings());
			material = mitem.getMaterial();

			switch (mitem.getType()) {
			case LIGHT_ARMOUR:
				lightDR+= mitem.getDefenceRating();
				light+= VanillaConfiguration.HELMET_RATIO;
				break;
			
			case HEAVY_ARMOUR:
				heavyDR+= mitem.getDefenceRating();
				heavy+= VanillaConfiguration.HELMET_RATIO;
				break;

			case EXOTIC_ARMOUR:
				exoticDR+= mitem.getDefenceRating();
				exotic+= VanillaConfiguration.HELMET_RATIO;
				break;
				
			default:
				break;
			}
			
			switch (material) {
			case LEATHER_LEGGINGS:
				armour+= VanillaConfiguration.LEATHER_LEGGINGS_MULTIPLIER;
				break;
			
			case GOLD_LEGGINGS:
				armour+= VanillaConfiguration.GOLD_LEGGINGS_MULTIPLIER;
				break;

			case CHAINMAIL_LEGGINGS:
				armour+= VanillaConfiguration.CHAINMAIL_LEGGINGS_MULTIPLIER;
				break;

			case IRON_LEGGINGS:
				armour+= VanillaConfiguration.IRON_LEGGINGS_MULTIPLIER;
				break;
			
			case DIAMOND_LEGGINGS:
				armour+= VanillaConfiguration.DIAMOND_LEGGINGS_MULTIPLIER;
				break;
				
			default:
				break;
			}
		}
		
		// Boots:
		if(inventory.getBoots() != null){
			mitem = MythrItem.fromBukkitItem(inventory.getBoots());
			material = mitem.getMaterial();

			switch (mitem.getType()) {
			case LIGHT_ARMOUR:
				lightDR+= mitem.getDefenceRating();
				light+= VanillaConfiguration.HELMET_RATIO;
				break;
			
			case HEAVY_ARMOUR:
				heavyDR+= mitem.getDefenceRating();
				heavy+= VanillaConfiguration.HELMET_RATIO;
				break;

			case EXOTIC_ARMOUR:
				exoticDR+= mitem.getDefenceRating();
				exotic+= VanillaConfiguration.HELMET_RATIO;
				break;
				
			default:
				break;
			}
			
			switch (material) {
			case LEATHER_BOOTS:
				armour+= VanillaConfiguration.LEATHER_BOOTS_MULTIPLIER;
				break;
			
			case GOLD_BOOTS:
				armour+= VanillaConfiguration.GOLD_BOOTS_MULTIPLIER;
				break;

			case CHAINMAIL_BOOTS:
				armour+= VanillaConfiguration.CHAINMAIL_BOOTS_MULTIPLIER;
				break;

			case IRON_BOOTS:
				armour+= VanillaConfiguration.IRON_BOOTS_MULTIPLIER;
				break;
			
			case DIAMOND_BOOTS:
				armour+= VanillaConfiguration.DIAMOND_BOOTS_MULTIPLIER;
				break;
				
			default:
				break;
			}
		}

		// Attributes:
		Attribute[] attributes = AttributeConfiguration.getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			int score = getSkillScore(attributes[i].getName());
			if(light > 0.0) lightDR+= light * attributes[i].getSpecifier(Specifier.LIGHT_ARMOUR_DEFENCE_RATING_MODIFIER, score);
			if(heavy > 0.0) heavyDR+= heavy * attributes[i].getSpecifier(Specifier.HEAVY_ARMOUR_DEFENCE_RATING_MODIFIER, score);
			if(exotic > 0.0) exoticDR+= exotic * attributes[i].getSpecifier(Specifier.EXOTIC_ARMOUR_DEFENCE_RATING_MODIFIER, score);
		}
		
		// Skills:
		Skill[] skills = SkillConfiguration.getSkills();
		for (int i = 0; i < skills.length; i++) {
			int score = getSkillScore(skills[i].getName());
			if(heavy > 0.0) lightDR+= heavy * skills[i].getSpecifier(Specifier.LIGHT_ARMOUR_DEFENCE_RATING_MODIFIER, score);
			if(heavy > 0.0) heavyDR+= heavy * skills[i].getSpecifier(Specifier.HEAVY_ARMOUR_DEFENCE_RATING_MODIFIER, score);
			if(exotic > 0.0) exoticDR+= exotic * skills[i].getSpecifier(Specifier.EXOTIC_ARMOUR_DEFENCE_RATING_MODIFIER, score);
		}
	 }

	/** Resets armour stats. */
	public void resetArmour()
	 {
		armour = 1.0;

		lightDR = 0;
		heavyDR = 0;
		exoticDR = 0;
	 }
	
	
	// SCORES:
	/**
	 * Gets the skill score.
	 * 
	 * @param skillName skill name
	 * @return score
	 */
	abstract protected int getSkillScore(String skillName);

	/**
	 * Gets the attribute score.
	 * 
	 * @param attribName attribute name
	 * @return score
	 */
	abstract protected int getAttributeScore(String attribName);
	
	
	// DAMAGE:
	/**
	 * 
	 * 
	 * @param type
	 * @param attacker
	 * @return
	 */
	public int defend(ItemType type, DerivedStats attacker)
	 {
		// Damage and defence:
		int damage = random(attacker.minBaseDmg, attacker.maxBaseDmg);
		double defenceRating = lightDR + heavyDR + exoticDR;
		double armour = this.armour;
		
		// Attack:
		double attackRating = 0;
		
		switch (type) {
		case MELEE_WEAPON:
			attackRating+= attacker.meleeAR;
			damage+= attacker.meleeDmgMod;
			break;
			
		case RANGED_WEAPON:
			attackRating+= attacker.rangedAR;
			damage+= attacker.rangedDmgMod;
			break;
			
		case MAGIC_WEAPON:
			attackRating+= attacker.magicAR;
			damage+= attacker.magicDmgMod;
			break;

		case CURSE_WEAPON:
			attackRating+= attacker.curseAR;
			damage+= attacker.curseDmgMod;
			break;

		case BLESSING_WEAPON:
			attackRating+= attacker.blessingAR;
			damage+= attacker.blessingDmgMod;
			break;
			
		default:
			break;
		}
		
		// Hit chance:
		double tohit = attackRating / (attackRating + defenceRating);
		boolean hit = tohit >= RANDOM.nextDouble();
		
		// Half armour on hit:
		if(hit){
			armour = armour / 2;
		}else{
			// Apply perks:
		}
		
		// Calculate damage:
		return LinearFunction.roundRand(damage*armour);
	 }
	
	
	// UTIL:
	/**
	 * Generates a random number between the given values.
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return random number
	 */
	public static int random(int min, int max) {
		return min + (int)(RANDOM.nextDouble() * ((max - min) + 1));
	}
	
}
