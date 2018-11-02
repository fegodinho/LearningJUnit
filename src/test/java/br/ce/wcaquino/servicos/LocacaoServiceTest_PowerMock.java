package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class})
public class LocacaoServiceTest_PowerMock {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));		
						
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
			//verificacao
			//assertEquals(5.0,locacao.getValor(), 0.01);
			//assertThat(locacao.getValor(),is(5.0));
			//assertThat(locacao.getValor(),is(equalTo(5.0)));
			error.checkThat(locacao.getValor(),is(equalTo(5.0)));
			//assertThat(locacao.getValor(),is(not(6.0)));
			
			//assertTrue(isMesmaData(locacao.getDataLocacao(), new Date())); //ou
			//assertThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			//error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()),is(true));
			//error.checkThat(locacao.getDataLocacao(), ehHoje());
			error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(),DataUtils.obterData(29, 4, 2017)), is(true));
			error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(),DataUtils.obterData(28, 4, 2017)), is(true));
			//assertTrue(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1))); //ou
			//assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
			//error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
			//error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
					
	}
	
	@Test
	public void deveDevovlerNaSegundaAoAlugarNoSabado() throws Exception {
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));
				
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY)); ou
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		//PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();		
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificacao
		Assert.assertThat(valor, is(4.0));		
	}
}
