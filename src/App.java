import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static final AudioClip alarmL = new AudioClip(new File("src\\Audio\\neverno.mp3").toURI().toString());
    private static final AudioClip alarmV = new AudioClip(new File("src\\Audio\\verno.mp3").toURI().toString());
    private static final AudioClip alarmW = new AudioClip(new File("src\\Audio\\Vibor.mp3").toURI().toString());
    private static final AudioClip alarmM = new AudioClip(new File("src\\Audio\\Menu.mp3").toURI().toString());
    Text text;
    DatagramSocket receiveSocket = new DatagramSocket(9876);
    DatagramSocket receiveSocketSystem = new DatagramSocket(9875);
    private static EventHandler<MouseEvent> mouseEventEventHandler1;
    private static EventHandler<MouseEvent> mouseEventEventHandler2;
    private static EventHandler<MouseEvent> mouseEventEventHandler3;
    String IP = "";
    double setConnect = 0.05;
    int [] numRand = new int[15];

    private String getIPHOST(String ip) {
        int index = 1;
        while (true) {
            if (ip.charAt(ip.length() - 1 - index) == '.') {
                break;
            } else index++;
        }
        ip = ip.substring(0, ip.length() - index) + 255;
        return ip;
    }

    private void randomOnline(){
        for (int i=0; i<15; i++){
            numRand[i]=(int) (Math.random() * 5);
        }
    }

    public App() throws SocketException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    String t = "";
    Label textLbl = new Label(t);
    private final Pane root = new Pane(); //панель
    int scoreOpponent = 0, numQuest = 0, questionOpponent = 0, scoreYour = 0, inGame=0;

    private ImageView setImg(String name, int setH, int setW, int setX, int setY) {
        Image image = new Image(getClass().getResourceAsStream(name));
        ImageView img = new ImageView(image);
        img.setFitHeight(setH);
        img.setFitWidth(setW);
        img.setTranslateX(setX);
        img.setTranslateY(setY);
        return img;
    }

    private static Text setTxt(String data, int setX, int setY, int size) {
        Text text = new Text(data);
        text.setTranslateX(setX);
        text.setTranslateY(setY);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("BROADWEY", FontWeight.BOLD, size));
        return text;
    }

    private void displayTray(String stringText) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("Img\\Kadr1.jpg"));
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("MillionaireBel 2.0", stringText, TrayIcon.MessageType.INFO);
    }

    private void gameCode(String code, InetAddress IPAddress, String str) throws AWTException, IOException {
        switch (code) {
            case "Ex":
                if (setConnect == 1) {
                    setConnect = 0.05;
                    displayTray("Оппонент покинул баттл");
                }
                break;
            case "Cr":
                t = "Sysyem::  User " + IPAddress.toString().substring(1) + " ответил верно на вопрос" + "\n" + t;
                System.out.println("Оппонент верно ответил");
                displayTray("Оппонент верно ответил на " + (questionOpponent + 1) + " вопрос");
                questionOpponent++;
                scoreOpponent++;
                break;
            case "Er":
                t = "Sysyem::  User " + IPAddress.toString().substring(1) + " ответил неверно на вопрос" + "\n" + t;
                System.out.println("Оппонент неверно ответил");
                displayTray("Оппонент неверно ответил на " + (questionOpponent + 1) + " вопрос");
                questionOpponent++;
                break;
            case "C?":
                for (int j=0; j<15; j++){
                    numRand[j]=str.charAt(j+2)-48;
                }
                IP = getIPHOST(IPAddress.toString().substring(1));
                System.out.println(IP);
                System.out.println("К вам хотят подключиться, кидаем обратку ок " + IPAddress.toString().substring(1));
                sendM("C&", IP, receiveSocketSystem.getLocalPort());
                displayTray("Connected");
                setConnect = 1;
                break;
            case "C&":
                IP = getIPHOST(IPAddress.toString().substring(1));
                System.out.println(IP);
                System.out.println("К вам подключились");
                displayTray("Connected");
                setConnect = 1;
                break;
            case "Ui":
                displayTray(IPAddress.toString().substring(1) + " вошёл в игру");
                break;
            case "Ue":
                displayTray(IPAddress.toString().substring(1) + " покинул игру");
                break;
        }
    }

    private Thread threadChat(){
        return new Thread(() -> {
            System.out.println("Поток приема сообщений создан");
            try {
                while (true) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiveSocket.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress();
                    String sentence = new String(receivePacket.getData());
                    if (!IPAddress.toString().equals("/" + InetAddress.getLocalHost().getHostAddress())) {
                        displayTray(IPAddress.toString().substring(1) + " :: " + sentence);
                        t = IPAddress.toString().substring(1) + " :: " + sentence + "\n" + t;
                    }
                }
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        });
    }

    private Thread threadSystem(){
        return new Thread(() -> {
            System.out.println("Игровой поток создан");
            try {
                while (true) {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    receiveSocketSystem.receive(receivePacket);
                    InetAddress IPAddress = receivePacket.getAddress();
                    String sentence = new String(receivePacket.getData());
                    System.out.println(IPAddress.toString() + " : " + sentence);
                    if (!IPAddress.toString().equals("/" + InetAddress.getLocalHost().getHostAddress())) {
                        gameCode(sentence.substring(0, 2), IPAddress, new String(receivePacket.getData()));
                    }
                }
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TextField textFieldIp = new TextField();
        Text textOk = setTxt("Вы подключены", 550, 20, 15);

        try {
            IP = getIPHOST(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Thread th = threadChat();
        Thread thSys = threadSystem();
        th.start();
        thSys.start();
        sendM("Ui", IP, receiveSocketSystem.getLocalPort());
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setOnCloseRequest(e -> {
            try {
                sendM("Ue", IP, receiveSocketSystem.getLocalPort());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.exit(0);
        });
        ImageView img = setImg("img/Заставка V1.jpg", 700, 1200, 0, 0);
        ImageView imgfon = setImg("img/Фон.jpg", 700, 1200, 0, 0);
        ImageView img1 = setImg("img/груст.gif", 450, 450, 40, 130);
        ImageView imgNo = setImg("img/весел.gif", 500, 500, 10, 120);
        ImageView imgQRcode = setImg("img/qr-code.gif", 120, 120, 50, 510);
        ImageView imgQuest = setImg("img/Question.gif", 500, 400, 150, 100);
        ImageView load = setImg("img/загрузка.gif", 300, 400, 640, 212);
        ImageView imgQuestOnline = setImg("img/UrB9.gif", 510, 550, 550, 80);
        ImageView img0 = setImg("img/Kadr1.jpg", 700, 1200, 0, 0);
        imgfon.setOpacity(0);
        img1.setOpacity(0);
        imgNo.setOpacity(0);
        root.getChildren().addAll(img, imgfon, img1, imgNo, img0);
        ImageView imgInfo = setImg("img/infoMy.gif", 324, 360, 780, 370);
        ImageView imgLose = setImg("img/lose.gif", 500, 383, 150, 110);
        ImageView imgWin = setImg("img/Win.gif", 500, 410, 150, 110);

        MenuItem exitYes = new MenuItem("Да");
        MenuItem exitNo = new MenuItem("Нет");
        exitNo.setTranslateY(40);
        exitYes.setTranslateY(40);
        exitNo.setTranslateX(-135);
        exitYes.setTranslateX(-135);
        SubMenu exitMenu = new SubMenu(exitYes, exitNo);

        MenuItem newGame = new MenuItem("Одиночная игра");
        MenuItem onlineGame = new MenuItem("Интеллектуальный баттл");
        MenuItem infoGame = new MenuItem("Об игре");
        MenuItem exitGame = new MenuItem("Выход");
        SubMenu mainMenu = new SubMenu(newGame, onlineGame, infoGame, exitGame);

        MenuItem returnGameWin = new MenuItem("В главное меню");
        returnGameWin.setTranslateY(320);
        returnGameWin.setTranslateX(-330);
        SubMenu pastWinMenu = new SubMenu(returnGameWin);

        MenuItem connectIP = new MenuItem("Подключение");
        MenuItem toGame = new MenuItem("Играть");
        MenuItem infoGameBackO = new MenuItem("Назад");
        SubMenu infoMenuO = new SubMenu(connectIP, toGame, infoGameBackO);

        MenuItem infoGameBack = new MenuItem("Назад");
        SubMenu infoMenu = new SubMenu(infoGameBack);

        Text textExit = setTxt("Вы уверены, что хотите выйти из игры?", 350, 100, 26);
        Text textWin = setTxt("YOU WIN", 620, 335, 75);
        Text textLoose = setTxt("YOU LOSE", 600, 335, 75);
        Text textQR = setTxt("- ссылка для обратной связи", 210, 575, 24);
        Text textPass = setTxt("- Пропуск вопроса", 70, 635, 18);
        Text text50 = setTxt("- 50/50", 70, 575, 18);
        Text textChance = setTxt("- Право на ошибку", 70, 605, 18);
        Text textInfo = setTxt("В данном программном игровом средстве каждый участник\n" +
                "может проверить свой интеллект и эрудицию в разных\n" +
                "категориях вопросов. Каждый раунд имеет 15 вопросов\n" +
                "из разных категорий. Каждый вопрос имеет\n" +
                "4 варианта ответа, из которых только один является верным.\n" +
                "\n" + "\n" + "Данное программное игровое средство разработано и создано студентом\n" +
                "группы 951006 БГУИР Курбацким Ильёй Дмитриевичем в 2021 году.", 90, 180, 20);
        Text textError = setTxt("Ошибка", 220, 335, 75);
        textError.setFill(Color.RED);
        ScrollPane scrollPane = new ScrollPane(textLbl);
        Button btn = new Button("update");
        TextField textFieldChat = new TextField();
        textFieldChat.setMinSize(467, 31);
        textFieldChat.setPrefColumnCount(20);
        textFieldChat.setTranslateY(590);
        textFieldChat.setTranslateX(60);
        textFieldIp.setText(IP);
        textFieldIp.setMinHeight(40);
        textFieldIp.setMinWidth(534);
        textFieldIp.setPrefColumnCount(20);
        textFieldIp.setTranslateY(40);
        textFieldIp.setTranslateX(60);
        scrollPane.setPrefViewportHeight(480);
        scrollPane.setPrefViewportWidth(514);
        scrollPane.setPannable(false);
        scrollPane.setVvalue(0.5);
        scrollPane.setHvalue(0.5);
        scrollPane.setTranslateX(60);
        scrollPane.setTranslateY(90);
        scrollPane.setContent(textLbl);
        btn.setTranslateX(527);
        btn.setTranslateY(590);

        MenuBox menuBox = new MenuBox(mainMenu);
        newGame.setOnMouseClicked(event -> {
            try {
                alarmM.stop();
                alarmW.setCycleCount(AudioClip.INDEFINITE);
                alarmW.play();
                root.getChildren().addAll(text50, textPass, textChance, imgQuest);
                root.getChildren().remove(img0);
                imgfon.setOpacity(1);
                Question(-1, menuBox, root, imgfon, textPass, text50, imgQuest, pastWinMenu, imgLose, textLoose, textWin, imgWin,
                        textChance, textError, mainMenu, returnGameWin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        connectIP.setOnMouseClicked(event -> {
            try {
                IP = textFieldIp.getText();
                String masquest="";
                randomOnline();
                for(int k=0; k<15; k++){
                    masquest+=numRand[k];
                }
                sendM("C?"+masquest, IP, receiveSocketSystem.getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menuBox.setOnMouseEntered(event -> {
            textOk.setOpacity(setConnect);
            textLbl.setText(t);
            textFieldIp.setText(IP);
            if (questionOpponent >= numQuest) {
                root.getChildren().remove(load);
                imgQuestOnline.setOpacity(0.3);
                if(inGame==1) { text.setOpacity(1);}
            } else {
                text.setOpacity(0);
                root.getChildren().remove(load);
                root.getChildren().add(load);
                imgQuestOnline.setOpacity(0);
            }
        });
        imgQuestOnline.setOnMouseEntered(event -> {
            if (questionOpponent == numQuest) {
                if(inGame==1) { text.setOpacity(1);}
                root.getChildren().remove(load);
                imgQuestOnline.setOpacity(0.3);
            }
        });
        toGame.setOnMouseClicked(event -> {
            if (setConnect == 1) {
                inGame=1;
                scrollPane.setPrefViewportWidth(450);
                textFieldChat.setMinWidth(407);
                textFieldChat.setMaxWidth(408);
                btn.setTranslateX(462);
                root.getChildren().removeAll(textFieldIp, menuBox);
                root.getChildren().addAll(imgQuestOnline, menuBox);
                try {
                    QuestionOnline(textFieldChat, btn, textOk, scrollPane, imgQuestOnline, load, -1, menuBox, root, imgfon, pastWinMenu, imgLose, textLoose, textWin, imgWin,
                            textError, mainMenu, returnGameWin);
                    alarmM.stop();
                    setConnect = scoreYour = scoreOpponent = questionOpponent = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        imgfon.setOnMouseEntered(event -> {
            textOk.setOpacity(setConnect);
            textLbl.setText(t);
            textFieldIp.setText(IP);
            if (questionOpponent == numQuest) {
                if(inGame==1) { text.setOpacity(1);}
                root.getChildren().remove(load);
                imgQuestOnline.setOpacity(0.3);
            }
        });
        infoGameBackO.setOnMouseClicked(event -> {
            menuBox.setSubMenu(mainMenu);
            imgfon.setOpacity(0);
            root.getChildren().removeAll(textFieldChat, textFieldIp, scrollPane, btn, textOk);
        });
        onlineGame.setOnMouseClicked(event -> {
            textFieldChat.setMinSize(467, 31);
            scrollPane.setPrefViewportHeight(480);
            scrollPane.setPrefViewportWidth(514);
            btn.setTranslateX(527);
            textOk.setOpacity(setConnect);
            root.getChildren().add(textOk);
            menuBox.setSubMenu(infoMenuO);
            imgfon.setOpacity(1);
            textFieldIp.setPromptText("IP of your opponent");
            root.getChildren().addAll(textFieldChat, textFieldIp);
            textLbl.setText(t);
            btn.setOnMouseClicked(event1 -> {
                try {
                    if (!textFieldChat.getText().equals("")) {
                        t = InetAddress.getLocalHost().getHostAddress() + " :: " + textFieldChat.getText() + "\n" + t;
                        sendM(textFieldChat.getText() + "\n", IP, receiveSocket.getLocalPort());
                    }
                    textLbl.setText(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                textFieldChat.setText("");
            });
            root.getChildren().addAll(scrollPane, btn);
        });
        infoGame.setOnMouseClicked(event -> {
            menuBox.setSubMenu(infoMenu);
            imgfon.setOpacity(1);
            root.getChildren().addAll(imgInfo, textQR, imgQRcode, textInfo);
        });
        infoGameBack.setOnMouseClicked(event -> {
            menuBox.setSubMenu(mainMenu);
            imgfon.setOpacity(0);
            root.getChildren().removeAll(imgInfo, textQR, textInfo, imgQRcode);
        });
        exitGame.setOnMouseClicked(event -> {
            menuBox.setSubMenu(exitMenu);
            imgfon.setOpacity(1);
            root.getChildren().add(textExit);
        });
        exitYes.setOnMouseClicked(event -> {
            try {
                sendM("Ue", IP, receiveSocketSystem.getLocalPort());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.exit(0);
        });
        exitYes.setOnMouseEntered(event -> img1.setOpacity(1));
        exitYes.setOnMouseExited(event -> img1.setOpacity(0));
        exitNo.setOnMouseClicked(event -> {
            menuBox.setSubMenu(mainMenu);
            imgfon.setOpacity(0);
            img.setOpacity(1);
            root.getChildren().remove(textExit);
        });
        exitNo.setOnMouseEntered(event -> imgNo.setOpacity(1));
        exitNo.setOnMouseExited(event -> imgNo.setOpacity(0));
        img0.setOnMouseClicked(event -> {
            alarmM.setCycleCount(AudioClip.INDEFINITE);
            alarmM.play(0.4f);
            root.getChildren().addAll(menuBox);
            menuBox.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.seconds(3), menuBox);
            FadeTransition ft1 = new FadeTransition(Duration.seconds(1), img0);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            menuBox.setVisible(true);
            ft1.setFromValue(1);
            ft1.setToValue(0);
            ft1.setOnFinished(evt -> img0.setVisible(false));
            ft1.play();
        });

        primaryStage.setTitle("MillionaireBel 2.0");
        setIcon(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendM(String sentence, String IP1, int port) throws IOException {
            System.out.println("to IP::" + IP1 + " send");
            DatagramSocket sendSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(IP);
            byte[] sendData = sentence.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendSocket.send(sendPacket);
    }

    private static List<String> readFile(String link) throws IOException {
        List<String> file_strings = new ArrayList<>();
        File file = new File(link);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        while ((str = br.readLine()) != null) {
            file_strings.add(str);
        }
        return file_strings;
    }

    private void QuestionOnline(TextField textFieldChat, Button btn, Text textOk, ScrollPane scrollPane, ImageView imgQuestOnline, ImageView load, int i, MenuBox menuBox, Pane root, ImageView imgfon, SubMenu pastWinMenu, ImageView imgLose, Text textLoose, Text textWin, ImageView imgWin,
                                Text textError, SubMenu mainMenu, MenuItem returnGameWin) throws IOException {
        i++;
        numQuest = i;
        imgfon.setOpacity(1);
        root.getChildren().remove(textError);
        Text textNumberT = setTxt("Вы покинули баттл:  " + scoreYour, 230, 345, 60);
        returnGameWin.setOnMouseClicked(event -> {
            menuBox.setSubMenu(mainMenu);
            alarmM.setCycleCount(AudioClip.INDEFINITE);
            alarmM.play(0.4f);
            imgfon.setOpacity(0);
            root.getChildren().removeAll(textNumberT, textLoose, textWin, imgLose, imgWin);
        });
        if (i < 15) {
            List<String> readA = readFile("src\\Игровые файлы\\Ответы.txt");
            List<String> readQ = readFile("src\\Игровые файлы\\Вопросы.txt");
            int[] arr = createRandom();
            Text textNumber = setTxt("Question:  " + (i + 1) + "\nScore:  " + scoreYour + "\nScore opponent:  " + scoreOpponent, 950, 110, 20);
            text = setTxt(readQ.get(i * 5 + numRand[i]), 100, 60, 20);
            root.getChildren().addAll(textNumber, text);
            MenuItem[] mas = createMenu(numRand[i], arr, readA, i);
            for (MenuItem ma : mas) {
                ma.setTranslateX(-100);
            }
            SubMenu QuestionOne = new SubMenu(mas[0], mas[3], mas[2], mas[1]);
            menuBox.setSubMenu(QuestionOne);
            int finalI = i;
            mas[arr[1]].setOnMouseClicked(event -> {
                try {
                    alarmV.stop();
                    alarmV.play();
                    sendM("Cr", IP, receiveSocketSystem.getLocalPort());
                    t = "Sysyem::  User " + InetAddress.getLocalHost().getHostAddress() + " ответил верно на вопрос" + "\n" + t;
                    textLbl.setText(t);
                    root.getChildren().removeAll(text, textNumber);
                    scoreYour++;
                    QuestionOnline(textFieldChat, btn, textOk, scrollPane, imgQuestOnline, load, finalI, menuBox, root, imgfon, pastWinMenu, imgLose, textLoose, textWin, imgWin, textError, mainMenu, returnGameWin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            textNumber.setOnMouseClicked(event -> {
                try {
                    sendM("Ex", IP, receiveSocketSystem.getLocalPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                alarmW.stop();
                menuBox.setSubMenu(pastWinMenu);
                root.getChildren().add(textNumberT);
                questionOpponent = numQuest = 0;
                setConnect = 0.05;
                try {
                    displayTray("Disconnect");
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                root.getChildren().removeAll(text, textNumber, scrollPane, imgQuestOnline, textFieldChat, btn, textOk, load);
            });

            EventHandler<MouseEvent> mouseEventEventHandler = event -> {
                try {
                    t = "Sysyem::  User " + InetAddress.getLocalHost().getHostAddress() + " ответил неверно на вопрос" + "\n" + t;
                    sendM("Er", IP, receiveSocketSystem.getLocalPort());
                    textLbl.setText(t);
                    root.getChildren().removeAll(text, textNumber);
                    QuestionOnline(textFieldChat, btn, textOk, scrollPane, imgQuestOnline, load, finalI, menuBox, root, imgfon, pastWinMenu, imgLose, textLoose, textWin, imgWin, textError, mainMenu, returnGameWin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            mas[arr[0]].setOnMouseClicked(mouseEventEventHandler);
            mas[arr[2]].setOnMouseClicked(mouseEventEventHandler);
            mas[arr[3]].setOnMouseClicked(mouseEventEventHandler);
        } else {
            menuBox.setSubMenu(pastWinMenu);
            setConnect = 0.05;
            inGame=0;
            if (scoreYour > scoreOpponent) {
                root.getChildren().addAll(textWin, imgWin);
            } else {
                root.getChildren().addAll(textLoose, imgLose);
                root.getChildren().removeAll(scrollPane, imgQuestOnline, textFieldChat, btn, textOk, load);
            }
        }
    }

    private static void Question(int i, MenuBox menuBox, Pane root, ImageView imgfon, Text textPass,
                                 Text text50, ImageView imgQuest, SubMenu pastWinMenu, ImageView imgLose, Text textLoose, Text textWin, ImageView imgWin,
                                 Text textChance, Text textError, SubMenu mainMenu, MenuItem returnGameWin) throws IOException {
        i++;
        imgfon.setOpacity(1);
        root.getChildren().remove(textError);
        Text textNumberT = setTxt("Ваш выигрыш равен   " + getSum(i), 170, 345, 60);
        returnGameWin.setOnMouseClicked(event -> {
            alarmM.setCycleCount(AudioClip.INDEFINITE);
            alarmM.play(0.4f);
            menuBox.setSubMenu(mainMenu);
            imgfon.setOpacity(0);
            root.getChildren().removeAll(textNumberT, textLoose, textWin, imgLose, imgWin);
        });
        if (i < 15) {
            List<String> readA = readFile("src\\Игровые файлы\\Ответы.txt");
            List<String> readQ = readFile("src\\Игровые файлы\\Вопросы.txt");
            int[] arr = createRandom();
            int uRandom = (int) (Math.random() * 5);
            Text textNumber = setTxt("" + getSum(i + 1), 1080, 50, 20);
            Text text = setTxt(readQ.get(i * 5 + uRandom), 100, 100, 20);
            root.getChildren().addAll(textNumber, text);
            MenuItem[] mas = createMenu(uRandom, arr, readA, i);
            SubMenu QuestionOne = new SubMenu(mas[0], mas[3], mas[2], mas[1]);
            menuBox.setSubMenu(QuestionOne);
            int finalI = i;
            mas[arr[1]].setOnMouseClicked(event -> {
                try {
                    alarmV.stop();
                    alarmV.play();
                    root.getChildren().removeAll(text, textNumber);
                    Question(finalI, menuBox, root, imgfon, textPass, text50, imgQuest, pastWinMenu, imgLose, textLoose, textWin, imgWin, textChance, textError, mainMenu, returnGameWin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            textPass.setOnMouseClicked(event -> {
                try {
                    root.getChildren().removeAll(text, textNumber, textPass);
                    Question(finalI, menuBox, root, imgfon, textPass, text50, imgQuest, pastWinMenu, imgLose, textLoose, textWin, imgWin, textChance, textError, mainMenu, returnGameWin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            text50.setOnMouseClicked(event -> {
                mas[arr[0]].setOpacity(0);
                mas[arr[3]].setOpacity(0);
                root.getChildren().remove(text50);
                add50(mas, arr);
            });
            textNumber.setOnMouseClicked(event -> {
                alarmW.stop();
                menuBox.setSubMenu(pastWinMenu);
                root.getChildren().removeAll(textPass, text50, textChance, imgQuest, text, textNumber);
                root.getChildren().add(textNumberT);

            });

            EventHandler<MouseEvent> mouseEventEventHandler = event -> {
                alarmV.stop();
                alarmW.stop();
                alarmL.play();
                root.getChildren().removeAll(textPass, text50, textChance, imgQuest, text, textNumber);
                root.getChildren().addAll(textLoose, imgLose);
                menuBox.setSubMenu(pastWinMenu);
            };
            mas[arr[0]].setOnMouseClicked(mouseEventEventHandler);
            mas[arr[2]].setOnMouseClicked(mouseEventEventHandler);
            mas[arr[3]].setOnMouseClicked(mouseEventEventHandler);

            final int[] index = {1, 0};
            mouseEventEventHandler1 = event -> {
                alarmV.stop();
                alarmL.play();
                if (index[0] == 1) {
                    menuBox.setSubMenu(QuestionOne);
                    index[0]++;
                    root.getChildren().add(textError);
                    mas[arr[0]].setOpacity(0);
                    index[1] = 1;
                } else {
                    if (index[1] != 1) {
                        root.getChildren().removeAll(textPass, text50, textChance, imgQuest, text, textNumber, textError);
                        root.getChildren().addAll(textLoose, imgLose);
                        menuBox.setSubMenu(pastWinMenu);
                        alarmW.stop();
                    } else {
                        alarmL.stop();
                    }
                }
            };

            mouseEventEventHandler2 = event -> {
                alarmV.stop();
                alarmL.play();
                if (index[0] == 1) {
                    menuBox.setSubMenu(QuestionOne);
                    index[0]++;
                    root.getChildren().add(textError);
                    mas[arr[2]].setOpacity(0);
                    index[1] = 2;
                } else {
                    if (index[1] != 2) {
                        root.getChildren().removeAll(textPass, text50, textChance, imgQuest, text, textNumber, textError);
                        root.getChildren().addAll(textLoose, imgLose);
                        menuBox.setSubMenu(pastWinMenu);
                        alarmW.stop();
                    } else {
                        alarmL.stop();
                    }
                }
            };

            mouseEventEventHandler3 = event -> {
                alarmV.stop();
                alarmL.play();
                if (index[0] == 1) {
                    menuBox.setSubMenu(QuestionOne);
                    index[0]++;
                    root.getChildren().add(textError);
                    mas[arr[3]].setOpacity(0);
                    index[1] = 3;
                } else {
                    if (index[1] != 3) {
                        root.getChildren().removeAll(textPass, text50, textChance, imgQuest, text, textNumber, textError);
                        root.getChildren().addAll(textLoose, imgLose);
                        menuBox.setSubMenu(pastWinMenu);
                        alarmW.stop();
                    } else {
                        alarmL.stop();
                    }
                }
            };

            textChance.setOnMouseClicked(event -> {
                root.getChildren().remove(textChance);
                mas[arr[0]].setOnMouseClicked(mouseEventEventHandler1);
                mas[arr[2]].setOnMouseClicked(mouseEventEventHandler2);
                mas[arr[3]].setOnMouseClicked(mouseEventEventHandler3);
            });

        } else {
            alarmW.stop();
            root.getChildren().addAll(textWin, imgWin);
            menuBox.setSubMenu(pastWinMenu);
            root.getChildren().removeAll(textChance, text50, textPass, imgQuest);
        }
    }

    private static MenuItem[] createMenu(int uRandom, int[] arr, List<String> readA, int i) {
        MenuItem Yes = new MenuItem(readA.get((i * 20 + 4 * uRandom) + 1));
        MenuItem NoV1 = new MenuItem(readA.get((i * 20 + 4 * uRandom)));
        MenuItem NoV2 = new MenuItem(readA.get((i * 20 + 4 * uRandom) + 2));
        MenuItem NoV3 = new MenuItem(readA.get((i * 20 + 4 * uRandom) + 3));
        MenuItem[] mas = new MenuItem[4];
        mas[arr[0]] = NoV1;
        mas[arr[1]] = Yes;
        mas[arr[2]] = NoV2;
        mas[arr[3]] = NoV3;
        return mas;
    }

    private static int getSum(int i) {
        int[] mas = {0, 100, 200, 300, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 125000, 250000, 500000, 1000000};
        return mas[i];
    }

    private static void add50(MenuItem[] mas, int[] arr) {
        mas[arr[0]].setOnMouseClicked(event -> {
        });
        mas[arr[3]].setOnMouseClicked(event -> {
        });
    }

    private static int[] createRandom() {
        int[] a = {0, 1, 2, 3};
        int pos1, pos2, temp;
        for (int i = 0; i < 20; i++) {
            pos1 = (int) (Math.random() * a.length);
            pos2 = (int) (Math.random() * a.length);
            temp = a[pos1];
            a[pos1] = a[pos2];
            a[pos2] = temp;
        }
        return a;
    }

    private void setIcon(Stage stage) {
        Image iconOfApplication = new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(iconOfApplication);
    }
}