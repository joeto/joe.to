package to.joe.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import to.joe.J2;

public class Recipes {
	J2 j2;
	public Recipes(J2 j2){
		this.j2=j2;
	}
	public void addRecipes(){
		if(j2.servernumber!=3){
			return;
		}
		//Diamond
		ShapedRecipe bob=new ShapedRecipe(new ItemStack(Material.DIAMOND,1));		
		bob.shape("ABA","BCB","ADA");
		bob.setIngredient('A', Material.STONE);
		bob.setIngredient('D', Material.LAVA_BUCKET);
		bob.setIngredient('C', Material.COAL);
		bob.setIngredient('B', Material.IRON_BLOCK);
		this.j2.getServer().addRecipe(bob);
		//Leaves
		ShapedRecipe bob2=new ShapedRecipe(new ItemStack(Material.LEAVES,2));		
		bob2.shape("  a"," b "," c ");
		bob2.setIngredient('a', Material.WATCH);
		bob2.setIngredient('b', Material.WATER_BUCKET);
		bob2.setIngredient('c', Material.SAPLING);
		this.j2.getServer().addRecipe(bob2);
		//Apple
		ShapedRecipe bob3=new ShapedRecipe(new ItemStack(Material.APPLE,1));		
		bob3.shape("aaa","aba"," b ");
		bob3.setIngredient('a', Material.LEAVES);
		bob3.setIngredient('b', Material.LOG);
		this.j2.getServer().addRecipe(bob3);
		//Netherrack
		ShapedRecipe bob4=new ShapedRecipe(new ItemStack(Material.NETHERRACK,1));		
		bob4.shape("AAA","ABA","AAA");
		bob4.setIngredient('A', Material.OBSIDIAN);
		bob4.setIngredient('B', Material.FLINT_AND_STEEL);
		this.j2.getServer().addRecipe(bob4);
		//Glowstone Block
		ShapedRecipe bob5=new ShapedRecipe(new ItemStack(Material.GLOWSTONE,20));		
		bob5.shape("AAA","ABA","AAA");
		bob5.setIngredient('A', Material.OBSIDIAN);
		bob5.setIngredient('B', Material.TORCH);
		this.j2.getServer().addRecipe(bob5);
		//Web
		ShapedRecipe bob6=new ShapedRecipe(new ItemStack(Material.WEB,1));		
		bob6.shape("A A"," A ","A A");
		bob6.setIngredient('A', Material.STRING);
		this.j2.getServer().addRecipe(bob6);
		//Soulsand
		ShapedRecipe bob7=new ShapedRecipe(new ItemStack(Material.SOUL_SAND,20));		
		bob7.shape("AAA","ABA","AAA");
		bob7.setIngredient('A', Material.OBSIDIAN);
		bob7.setIngredient('B', Material.SAND);
		this.j2.getServer().addRecipe(bob7);
	}
}
