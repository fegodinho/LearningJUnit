package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {
	
	@Test
	public void test() {
		
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals("Erro de comparacao",1,1);
		Assert.assertEquals(0.512345, 0.512344, 0.00001);
		Assert.assertEquals(Math.PI, 3.145, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("bola", "bola");
		Assert.assertNotEquals("bola", "casa");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Assert.assertEquals(u1, u2); //verifica se os objetos são iguais. fez uso do método equals
									//da classe Usuario para comparar os atributos
		Assert.assertSame(u2, u2); //compara a instacia dos objetos
		Assert.assertNotSame(u1, u2);
		
		Usuario u3 = null;
		Assert.assertTrue(u3==null);
		Assert.assertNull(u3);
		Assert.assertNotNull(u2);
		
		
		
		
	}

}
