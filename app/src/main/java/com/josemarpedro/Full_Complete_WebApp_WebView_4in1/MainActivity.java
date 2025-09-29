package com.josemarpedro.Full_Complete_WebApp_WebView_4in1; // 👉 Substitua se o seu pacote for diferente

import android.annotation.SuppressLint;          // Importa anotação para suprimir avisos de lint
import android.content.Intent;                  // Para abrir intents (apps externos)
import android.net.Uri;                         // Para manipular URIs
import android.os.Bundle;                       // Bundle para estado da Activity
import android.view.View;                       // Para manipular views
import android.webkit.WebResourceError;        // Para erros de carregamento web
import android.webkit.WebResourceRequest;      // Para requisições na WebView
import android.webkit.WebSettings;              // Configurações da WebView
import android.webkit.WebView;                  // Componente WebView
import android.webkit.WebViewClient;            // Cliente para controlar navegação
import android.widget.Button;                   // Botão UI
import android.widget.LinearLayout;             // Layout linear
import android.widget.Toast;                    // Para mostrar mensagens rápidas

import androidx.appcompat.app.AppCompatActivity;  // Activity compatível com ActionBar
import androidx.core.view.WindowCompat;            // Para manipular a janela do app

import java.io.IOException;                     // Exceções de IO
import java.net.HttpURLConnection;              // Conexões HTTP
import java.net.URL;                            // URL para conexão
import java.util.concurrent.Executor;           // Executor para threads
import java.util.concurrent.Executors;          // Criador de executors

public class MainActivity extends AppCompatActivity {  // Declara a Activity principal

    // Elementos da interface
    private WebView webView;                       // WebView para mostrar site
    private LinearLayout offlineLayout;            // Layout para tela offline
    private Button retryButton;                     // Botão para tentar recarregar

    // Executor para tarefas de rede em segundo plano
    private final Executor executor = Executors.newSingleThreadExecutor(); // Thread única para rede

    // Tempo máximo de resposta ao testar conexão
    private static final int TIMEOUT_MS = 5000;    // Timeout 5 segundos

    // Contador para tentativas automáticas de carregamento
    private int retryAttempts = 0;                  // Contador de tentativas
    private static final int MAX_RETRY = 2;         // Máximo de tentativas

    @SuppressLint("SetJavaScriptEnabled")          // Suprime aviso de habilitar JS
    @Override
    protected void onCreate(Bundle savedInstanceState) {  // Método chamado ao criar Activity
        super.onCreate(savedInstanceState);

        // Faz a status bar usar a cor padrão do sistema (remover cor lilás)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        // Carrega o layout principal da atividade
        setContentView(R.layout.activity_main);

        // Mostra aviso enquanto carrega
        Toast.makeText(this, "A Carregar...", Toast.LENGTH_LONG).show();

        // Liga os elementos do layout às variáveis
        webView = findViewById(R.id.webview);
        offlineLayout = findViewById(R.id.offlineLayout);
        retryButton = findViewById(R.id.retryButton);

        // Configurações da WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);                        // Ativa JavaScript
        webSettings.setDomStorageEnabled(true);                        // Ativa localStorage
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // Usa cache se estiver offline
        webSettings.setLoadsImagesAutomatically(true);                 // Carrega imagens automaticamente
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Permite HTTPS + HTTP

        // Define o comportamento da WebView
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) { // Ao clicar num link
                String url = request.getUrl().toString();

                // Verifica se o link é externo e deve ser aberto por outro app
                if (url.startsWith("whatsapp:") || url.contains("wa.me") ||
                        url.startsWith("tel:") || url.startsWith("mailto:") ||
                        url.contains("instagram.com") || url.contains("facebook.com") ||
                        url.contains("maps.google.") || url.contains("youtube.com") ||
                        url.startsWith("intent://")) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); // Cria intent para abrir app externo
                        startActivity(intent); // Tenta abrir
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Nenhum app disponível para abrir o link", Toast.LENGTH_SHORT).show(); // Erro ao abrir
                    }

                    return true; // Intercepta link externo para não abrir na WebView
                }

                return false; // Continua carregando na WebView
            }

            @Override
            public void onPageFinished(WebView view, String url) { // Quando a página termina de carregar
                super.onPageFinished(view, url);

                // Quando a página terminar de carregar, mostra a WebView e esconde o layout offline
                webView.setVisibility(View.VISIBLE);          // Mostra WebView
                offlineLayout.setVisibility(View.GONE);       // Esconde tela offline
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) { // Erro no carregamento
                // Mostra tela offline em caso de erro de carregamento
                showOfflineScreen();                           // Mostra erro offline
            }
        });

        // Inicia o carregamento verificando a conexão
        checkInternetAndLoad();

        // Botão "Tentar Novamente" recarrega a verificação
        retryButton.setOnClickListener(v -> {
            retryAttempts = 0;          // Reseta tentativas ao clicar
            checkInternetAndLoad();     // Tenta carregar novamente
        });
    }

    /**
     * Verifica a conexão real com a internet e tenta carregar o site.
     * Tenta automaticamente até 2 vezes antes de mostrar o erro.
     */
    private void checkInternetAndLoad() {
        executor.execute(() -> {                 // Executa em thread separada
            boolean isOnline = hasInternetAccess(); // Testa conexão real

            runOnUiThread(() -> {                // Atualiza UI na thread principal
                if (isOnline) {
                    retryAttempts = 0;            // Reseta contador de tentativas
                    webView.loadUrl(getString(R.string.site)); // Carrega site da string
                    webView.setVisibility(View.VISIBLE);       // Mostra WebView
                    offlineLayout.setVisibility(View.GONE);    // Esconde tela offline
                } else {
                    if (retryAttempts < MAX_RETRY) {           // Se ainda pode tentar
                        retryAttempts++;                        // Incrementa tentativas
                        checkInternetAndLoad();                 // Tenta novamente recursivamente
                    } else {
                        showOfflineScreen();                    // Depois de tentativas falhas, mostra erro
                    }
                }
            });
        });
    }

    /**
     * Verifica se há acesso real à internet testando conexão com o site do app.
     */
    private boolean hasInternetAccess() {
        try {
            URL url = new URL(getString(R.string.site)); // Usa o site definido no strings.xml
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Abre conexão HTTP
            connection.setConnectTimeout(TIMEOUT_MS);       // Timeout conexão
            connection.setReadTimeout(TIMEOUT_MS);          // Timeout leitura
            connection.setRequestProperty("User-Agent", "AndroidApp"); // User agent personalizado
            connection.setRequestProperty("Connection", "close");     // Fecha conexão após uso
            connection.connect();                            // Realiza conexão

            return connection.getResponseCode() == 200;     // Retorna true se HTTP 200 OK
        } catch (IOException e) {
            return false;                                    // Falha conexão = sem internet real
        }
    }

    /**
     * Mostra a tela offline quando não há conexão.
     */
    private void showOfflineScreen() {
        webView.setVisibility(View.GONE);          // Esconde WebView
        offlineLayout.setVisibility(View.VISIBLE); // Mostra tela offline
    }

    // Sistema para sair do app ao pressionar "Voltar" duas vezes
    private long backPressedTime = 0;             // Guarda tempo do último back press
    private Toast backToast;                       // Toast para aviso

    @Override
    public void onBackPressed() {                  // Ao pressionar botão voltar
        // Se puder voltar no histórico da WebView
        if (webView.canGoBack()) {
            webView.goBack();                       // Volta para página anterior
            return;
        }

        // Se pressionar duas vezes para sair
        if (backPressedTime + 3000 > System.currentTimeMillis()) { // Se apertou dentro de 3s
            if (backToast != null) backToast.cancel();             // Cancela aviso anterior
            super.onBackPressed();                                  // Sai do app
        } else {
            // Primeira vez: mostra aviso
            backToast = Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT);
            backToast.show();                                       // Mostra mensagem "pressione novamente"
        }

        backPressedTime = System.currentTimeMillis();               // Atualiza tempo do último back press
    }
}
