package dctre;


import dc.direitoconstitucionaltre2017.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.res.Resources;

///////////////////////////////////////// INÍCIO DA CLASSE ///////////////////////////////////////////////////////////////////////////////////////////////////	
public class Questao extends QuestaoConector implements OnItemSelectedListener{
	
///////////////////////////////// VARIÁVEIS PÚBLICAS /////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	// variável utilizada para trabalhar com o método checked
	private int z = -1;	

	// variável para trabalhar com o método embaralhar
	private int[] w = new int[h];

	// Variáveis para tratar com os objetos da tela2
	TextView tv;
	RadioButton[] rd = new RadioButton[5];
	Button btnRandom, btnChecar, btnBuscar, btnAnterior, btnProximo, btnSortear, btnTema;
	EditText txtBuscar;
	Spinner btnSpinner;

	// variáveis para impedir que o random repita números
	private int f = 0;
	
	// variável global para auxiliar contagem das questões temáticas
	private int auxiliarTema = 0;
	
	//Essas variáveis são utilizadas para trabalhar com o Spinner
	List <String> temas;
	List <Integer> conectorAuxiliar;	
	List <Integer> questoesTematicas;
	
///////////////////////////////// FIM DAS VARIÁVEIS PÚBLICAS /////////////////////////////////////////////////////////////////////////////////////////////////////////	

	
	
///////////////////////////////////////// INÍCIO DO MÉTODO PRINCIPAL ////////////////////////////////////////////////////////////////////////////////////////////////
	
	// método para trabalhar com a tela2
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		z = restaurarEstado();
		w = restaurarW();
		setContentView(R.layout.tela2);
		tv = (TextView) findViewById(R.id.textView1); // ligando os objetos das
														// telas às variáveis
		rd[0] = (RadioButton) findViewById(R.id.radio0);
		rd[1] = (RadioButton) findViewById(R.id.radio1);
		rd[2] = (RadioButton) findViewById(R.id.radio2);
		rd[3] = (RadioButton) findViewById(R.id.radio3);
		rd[4] = (RadioButton) findViewById(R.id.radio4);
		btnRandom = (Button) findViewById(R.id.btnRandomizar);
		btnChecar = (Button) findViewById(R.id.btnChecar);
		btnBuscar = (Button) findViewById(R.id.btnBuscar);
		btnAnterior = (Button)findViewById(R.id.btnAnterior);
		btnProximo = (Button)findViewById(R.id.btnProximo);
		btnSortear = (Button)findViewById(R.id.btnSortear);
		txtBuscar = (EditText) findViewById(R.id.editText1);
		btnSpinner = (Spinner) findViewById(R.id.escolhedor);
		btnTema = (Button) findViewById(R.id.btnTema);
		questoesTematicas = new ArrayList<Integer>();
		
		
		if(z!=-1){
			carregarQuestao(z);
			f=z;
		}

/////////////////////////////INÍCIO DOS MÉTODOS DOS BOTÕES////////////////////////////////////////////////////////////////		

		// método para trabalhar com o Sortear
		btnSortear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (f < h) {
					// o array w possui os números das questões embaralhadas, a
					// variável f irá orientar esse array, iniciando de 0 até ...
					z = w[f];
					carregarQuestao(z);
					f++; // incrementando a variável f que armazena a posição do
							// array que está sendo percorrido.
				}
			}
		});

		// método para trabalhar com o botão Checar
		btnChecar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				QuestaoAux.checked(z, b, rd, Questao.this);
			}
		});

		// método para trabalhar com o botão Buscar
		btnBuscar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				carregarQuestao(Integer.parseInt(txtBuscar.getText().toString()));
			}
		});
		
		//método para trabalhar com o botão Anterior
		btnAnterior.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (z > 0) {
					carregarQuestao(--z);
				}
			}
		});
		
		//método para trabalhar com o botão Próximo
		btnProximo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (z < h) {
					carregarQuestao(++z);
				}
			}
		});
		
		//método para trabalhar com o botão Random
		//Tudo que esse método faz é randomizar, apenas isso... 
		//Após clicar em Randomizar, é necessário clicar em Sortear para que
		//esse método faça sentido... Caso contrário, se o usuário ficar sempre clicando no
		//botão Randomizar, o array na posição w[0] sempre será atualizado, e não será reaproveitado as
		//outras posições
		btnRandom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				z=f=0;
				QuestaoAux.embaralhar(w);
				carregarQuestao(w[f]);
			}
		});
		
		//método para selecionar perguntas do mesmo tema, ele trabalha em conjunto com 
		// O CÓDIGO PARA TRABALHAR COM O SPINNER e com os
		// MÉTODOS PARA TRABALHAR COM O ITEM SELECIONADO PELO SPINNER
		btnTema.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				carregarQuestao(questoesTematicas.get(auxiliarTema++));				
			}
		});
		
/////////////////////////////FIM DOS MÉTODOS DOS BOTÕES////////////////////////////////////////////////////////////////		
				
		Resources rc = this.getResources();
		//QuestaoConector qc = new QuestaoConector();
		temas = new ArrayList<String>();
		temas.add("TODOS OS TEMAS"); //essa adição serve apenas para deixar o primeiro item vazio
		conectorAuxiliar = new ArrayList<Integer>();
		//enderecoDaQuestao = new ArrayList<Integer>();
				
		for(int i=0;i<get_QtdeDeQuestoes();i++){                      //esse laço permite percorrer por todas as questões
			String a = rc.getString(get_EnderecoDaQuestao(i));        //essa linha faz o resgate do enunciado da questão
			String b = (String) a.subSequence(a.indexOf("[")+1, a.indexOf("]"));	//essa linha faz a limpeza do enunciado da questão, retornando apenas o tema			
			if(temas.indexOf(b)==-1){                                    //essa linha verifica se o tema já existe na variável temas
				temas.add(b);												//se o tema não existir, ele será adicionado											
			}															
			conectorAuxiliar.add(temas.indexOf(b));	//essa variável irá armazenar a posição da variável temas correspondente a variável b
													//por que essa posição? Porque ela é única e mais fácil de processar do que uma String.
													//Assim, cada elemento desta lista terá um valor relacionado ao tema correspondente.
													//Num contexto geral, essa variável faz a ligação entre o tema e a questão do tema
		}		
		
		
		
		
///////////////////////INÍCIO DO CÓDIGO PARA TRABALHAR COM O SPINNER/////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		// O código abaixo foi retirado na internet especificamente do Tutorials Point
		// Segue o endereço https://www.tutorialspoint.com/android/android_spinner_control.htm
		// Alterei apenas a variável categorias por temas
		// Spinner click listener
	      btnSpinner.setOnItemSelectedListener((OnItemSelectedListener) this);
	      
	      // Creating adapter for spinner
	      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, temas);
	      
	      // Drop down layout style - list view with radio button
	      dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
	      
	      // attaching data adapter to spinner
	      btnSpinner.setAdapter(dataAdapter);			      
	      
///////////////////////FIM DO CÓDIGO PARA TRABALHAR COM O SPINNER/////////////////////////////////////////////////////////////////////////////////////////////////////////		      
	}
	
///////////////////////////////////////// FIM DO MÉTODO PRINCIPAL ////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
///////////////////////////////////////// INÍCIO DOS MÉTODOS AUXILIARES //////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
//////// MÉTODO PARA TRABALHAR COM O ITEM SELECIONADO PELO SPINNER/////////////////////////////////////////////
	@Override
	   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	      // On selecting a spinner item
	      String item = parent.getItemAtPosition(position).toString();
	      if(item!="TODOS OS TEMAS"){
	    	  carregarArrayComQuestoesDoTemaSelecionado(item);	
	    	  carregarQuestao(questoesTematicas.get(auxiliarTema++));
	      }
	      // Showing selected spinner item
	      Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();	      
	      
	   }
	   public void onNothingSelected(AdapterView<?> arg0) {
	      // TODO Auto-generated method stub
	   }
	   
	   public void carregarArrayComQuestoesDoTemaSelecionado(String tema){
		   int posicaoDoTema = temas.indexOf(tema);
		   int i = conectorAuxiliar.size();
		   for(int j=0;j<i;j++){
			   if(conectorAuxiliar.get(j) == posicaoDoTema){
				   questoesTematicas.add(j);
			   }
		   }
	   }
	   
////////FIM DO MÉTODO PARA TRABALHAR COM O ITEM SELECIONADO PELO SPINNER///////////////////////////////////////	   	  
	   
	   	   
	/**
	 * Serve para carregar uma questão na tela2
	 * @param questao
	 * precisa informar o número da questão por meio da variável questao
	 */
	public void carregarQuestao(int questao) {
		z = questao;										//* a variável z armazena o número da questão para verificar se ela está correta		 		 		
		tv.setText(a[questao]);								//* a variável tv carrega o enunciado na tela
		rd[0].setText(b[questao][0]);						//* o array rd carrega cada uma das alternativas
		rd[1].setText(b[questao][1]);
		rd[2].setText(b[questao][2]);
		rd[3].setText(b[questao][3]);
		rd[4].setText(b[questao][4]);
		txtBuscar.setText(String.valueOf(questao));			//* a variável txtBuscar carrega o número da questão na tela
	}
	/*
	 * Serve para guardar as variáveis atuais do app
	 * Permitindo que o app continue de onde parou
	 */
	public void guardarEstado(){
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("z", z);
		for(int i=0;i<h;i++){
			String cat="aux"+i;
			editor.putInt(cat, w[i]);
		}
		editor.commit();
	}
	
	/*
	 * Serve para restaurar uma variável guardada pelo método guardarEstado()
	 *  A variável que ele restaura é da última questão que foi apresentada na tela
	 */
	public int restaurarEstado(){
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		int defaultValue = -1;
		int value = sharedPref.getInt("z", defaultValue);
		return value;
	}
	
	/*
	 * Serve para restaurar o array guardado pelo método guardarEstado()
	 * O array que ele restaura é o utilizado para fazer o random
	 */
	public int[] restaurarW(){
		int[] aux=new int[h];
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		for(int i=0;i<h;i++){
			int defaultValue = i;
			aux[i]=sharedPref.getInt("aux"+i, defaultValue);
		}
		return aux;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		guardarEstado();
	}
	@Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
	@Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
	@Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }
///////////////////////////////////////// FIM DOS MÉTODOS AUXILIARES //////////////////////////////////////////////////////////////////////////////////////////////////		
}

///////////////////////////////////////// FIM DA CLASSE //////////////////////////////////////////////////////////////////////////////////////////////////	