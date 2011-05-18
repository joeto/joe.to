package to.joe.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import to.joe.J2Plugin;

public class Recipes {
	J2Plugin j2;
	public Recipes(J2Plugin j2){
		this.j2=j2;
	}
	public void addRecipes(){
		if(j2.servernumber!=3){
			return;
		}
		ShapedRecipe bob=new ShapedRecipe(new ItemStack(Material.DIAMOND,1));		
		bob.shape("ABA","BCB","ADA");
		bob.setIngredient('A', Material.STONE);
		bob.setIngredient('D', Material.LAVA_BUCKET);
		bob.setIngredient('C', Material.COAL);
		bob.setIngredient('B', Material.IRON_BLOCK);
		this.j2.getServer().addRecipe(bob);
		ShapedRecipe bob2=new ShapedRecipe(new ItemStack(Material.LEAVES,2));		
		bob2.shape("  a"," b "," c ");
		bob2.setIngredient('a', Material.WATCH);
		bob2.setIngredient('b', Material.WATER_BUCKET);
		bob2.setIngredient('c', Material.SAPLING);
		this.j2.getServer().addRecipe(bob2);
		ShapedRecipe bob3=new ShapedRecipe(new ItemStack(Material.APPLE,1));		
		bob3.shape("aaa","aba"," b ");
		bob3.setIngredient('a', Material.LEAVES);
		bob3.setIngredient('b', Material.LOG);
		this.j2.getServer().addRecipe(bob3);
		ShapedRecipe bob4=new ShapedRecipe(new ItemStack(Material.BEDROCK,1));		
		bob4.shape("aaa","aaa","aaa");
		bob4.setIngredient('a', Material.GOLDEN_APPLE);
		this.j2.getServer().addRecipe(bob4);
		ShapedRecipe bob5=new ShapedRecipe(new ItemStack(Material.PORTAL,1));		
		bob5.shape("aaa","a a","aba");
		bob5.setIngredient('a', Material.OBSIDIAN);
		bob5.setIngredient('b', Material.FLINT_AND_STEEL);
		this.j2.getServer().addRecipe(bob5);
		ShapedRecipe bob6=new ShapedRecipe(new ItemStack(Material.NETHERRACK,1));		
		bob6.shape("aaa","aba","aba");
		bob6.setIngredient('a', Material.OBSIDIAN);
		bob6.setIngredient('b', Material.PORTAL);
		this.j2.getServer().addRecipe(bob6);
		ShapedRecipe bob7=new ShapedRecipe(new ItemStack(Material.GLOWSTONE_DUST,1));		
		bob7.shape("a a"," a ","a a");
		bob7.setIngredient('a', Material.NETHERRACK);
		this.j2.getServer().addRecipe(bob7);
		ShapedRecipe bob8=new ShapedRecipe(new ItemStack(Material.WEB,1));		
		bob.shape("A A"," A ","A A");
		bob.setIngredient('A', Material.STRING);
		this.j2.getServer().addRecipe(bob8);
	}
}
