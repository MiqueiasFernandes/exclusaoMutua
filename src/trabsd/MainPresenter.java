/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabsd;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author mfernandes
 */
public class MainPresenter {

    MainView view;

    public MainPresenter() {
        this.view = new MainView();

        view.getConectarBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conectar();
            }
        });

        view.getbtnClear().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }

        });

        view.setVisible(true);
        view.getStatus().setBackground(Color.ORANGE);
        view.getStatus().repaint();
        servidor();
        assistente();
    }

    public void clear() {
        meu_proximo = null;
        view.getStatus().setBackground(Color.white);
        view.getStatus().repaint();
    }

    void servidor() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Instancia o ServerSocket ouvindo a porta 12345
                ServerSocket servidor = null;
                try {
                    servidor = new ServerSocket(12345);
                } catch (Exception e) {
                    System.out.println("Servidor Err: " + e.getMessage());
                }
                System.out.println("Servidor ouvindo a porta 12345");

                while (true) {
                    try {
                        // o método accept() bloqueia a execução até que
                        // o servidor receba um pedido de conexão
                        view.getcampoTxt().setBackground(Color.RED);
                        view.getStatus().setBackground(Color.red);
                        view.getStatus().repaint();
                        Socket cliente = servidor.accept();
                        System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                        ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

                        Mensagem mensagem = (Mensagem) entrada.readObject();
                        view.getStatus().setBackground(Color.blue);
                        view.getStatus().repaint();
                        cliente.close();

                        view.getjLabel3().setText("msg recebida id " + mensagem.id + " : " + mensagem.getMensagem());

                        view.getRecebido().setText(mensagem.getMensagem());

                        if (view.getMEUIpTxt().getText().equals("127.0.0.1")) {
                            view.getMEUIpTxt().setText(mensagem.getIp());
                            mensagem.addno(mensagem.getIp());
                        }

                        if (!view.getNosdarede().getText().equals(mensagem.getRede())) {
                            view.getNosdarede().setText(mensagem.getRede());
                        }

                        view.getMsgn().setText(">" + mensagem.getNovoId());

                        view.getcampoTxt().setBackground(Color.green);

                        Thread.sleep(Integer.parseInt(view.getjSpinner1().getValue().toString()));

                        while (!cliente(mensagem));

                    } catch (Exception e) {
                        System.out.println("Erro: " + e.getMessage());
                    }
                }

            }
        }).start();

    }

    String meu_proximo = null;

    boolean cliente(Mensagem mensagem) {

        try {

            mensagem.ordenar(view.getMEUIpTxt().getText(), meu_proximo);

            view.getStatus().setBackground(Color.GREEN);
            view.getStatus().repaint();
            Socket cliente = new Socket(meu_proximo, 12345);
            view.getjLabel3().setText("env msg id " + mensagem.id + " : " + mensagem.getMensagem());
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
            saida.flush();

            mensagem.ip = meu_proximo;

            saida.writeObject(mensagem);
            saida.close();
            cliente.close();

            System.out.println("Conexão encerrada");
            return true;

        } catch (Exception e) {
            System.out.println("cliente Erro: " + e.getMessage());
            return false;
        }
    }

    void assistente() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // Instancia o ServerSocket ouvindo a porta 12345
                    ServerSocket servidor = new ServerSocket(12346);
                    System.out.println("Servidor assistente ouvindo a porta 12346");
                    while (true) {
                        // o método accept() bloqueia a execução até que
                        // o servidor receba um pedido de conexão
                        Socket cliente = servidor.accept();
                        System.out.println("Cliente querendo conectar: " + cliente.getInetAddress().getHostAddress());
                        view.getStatus().setBackground(Color.black);
                        view.getStatus().repaint();
                        ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                        saida.flush();

                        boolean tentar = false;

                        if (meu_proximo == null) {////caso so tenha eu

                            if (view.getMEUIpTxt().getText().equalsIgnoreCase("127.0.0.1")) {
                                view.getMEUIpTxt().setText(cliente.getLocalAddress().getHostAddress());
                            }

                            saida.writeObject(view.getMEUIpTxt().getText());

                            tentar = true;
                            System.out.println("enviei meu ip: " + view.getMEUIpTxt().getText());

                        } else ///caso contrario
                        {
                            saida.writeObject(meu_proximo);
                            System.out.println("enviei ip do meu prox: " + meu_proximo);
                        }
                        meu_proximo = cliente.getInetAddress().getHostAddress();
                        view.getIpservidorLbl().setText("meu proximo:" + meu_proximo);
                        saida.close();
                        cliente.close();
                        if (tentar) {
                            Mensagem mensagem = new Mensagem(0, "oi", meu_proximo);
                            mensagem.addno(view.getMEUIpTxt().getText());
                            while (!cliente(mensagem)) {
                                Thread.sleep(100);
                            }
                        }

                    }
                } catch (Exception e) {
                    System.out.println("Erro: " + e.getMessage());
                }

            }
        }).start();
    }

    void conectar() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    view.getStatus().setBackground(Color.MAGENTA);
                    view.getStatus().repaint();
                    Socket cliente = new Socket(view.getIpTxt().getText(), 12346);
                    ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

                    meu_proximo = (String) entrada.readObject();

                    view.getIpservidorLbl().setText("meu proximo:" + meu_proximo);

                    entrada.close();
                    cliente.close();
                    System.out.println("Conexão encerrada");

                } catch (Exception e) {
                    System.out.println("Erro: " + e.getMessage());
                }

            }
        }).start();
    }

}
