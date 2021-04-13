package Controller;

import GUI.gui;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Control {

    gui puzzle = new gui();
    private int moveCount = 0;
    private Timer timer;
    private JButton[][] matrix;
    private boolean isGameStart = false;
    private int size = 3;

    public Control() {
        // initMatrix();
        runNewGame();
        newGame();
        puzzle.setLocationRelativeTo(null);
        puzzle.setVisible(true);
        puzzle.setResizable(false);
    }

    public void runNewGame() {
        this.initMoveCount();
        this.initTimeCount();
        this.initMatrix();
        isGameStart = true;
    }

    public void initMoveCount() {
        moveCount = 0;
        puzzle.lblCountMove.setText("0");
    }

    public void initTimeCount() {
        puzzle.lblCountTime.setText("0");
        timer = new Timer(1000, new ActionListener() {
            int second = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                second++;
                puzzle.lblCountTime.setText(String.valueOf(second));
            }
        });
        timer.start();
    }

    public void initMatrix() {

        String value = puzzle.cbxSize.getSelectedItem() + "";
        String[] arr = value.split("x");
        size = Integer.parseInt(arr[0]);

        // không đổi được trực tiếp 2 button vì muốn đổi 2 vị trí phải xóa button cũ đi và add button mới vào đúng vị trí đó
        // nhưng mà griplayout thì khi xóa xong thì tự dồn lên . add button mới thì sẽ tự động thêm vào cuối
        //remove size cũ đi để add size mới vào --> nếu không thì nó sẽ chứa cả hai size 
        puzzle.pnLayout.removeAll();//remove before add new
        puzzle.pnLayout.setLayout(new GridLayout(size, size, 10, 10));
        // trong panel size button size - 1 hgap vgap
        puzzle.pnLayout.setPreferredSize(new Dimension(size * 60 + (size - 1) * 10, size * 60 + (size - 1) * 10));

        // tạo ra một mảng 2 chiều với size hàng, cột và giá trị của 1 phần tử trong đó là 1 button
        matrix = new JButton[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                JButton button = new JButton(i * size + j + 1 + "");   //0 0 -> 1//0 1 ->2 //0-2 --> 3
                // add button vào matrix
                matrix[i][j] = button;
                puzzle.pnLayout.add(button);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isGameStart) {
                            // kiem tra button co move sang duoc o trong ko 
                            if (checkMove(button)) {
                                moveButton(button);
                                if (checkWin()) {
                                    isGameStart = false;
                                    if (moveCount == 1) { // khi button move thi time ++ 
                                        // khi win game thì time dừng lại . nếu không có thì khi win time vẫn chạy
                                        initTimeCount();
                                    }
                                    timer.stop();
                                    JOptionPane.showMessageDialog(null, "You won!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Press New Game to start");
//                            runNewGame();
                        }
                    }
                });
            }
        }
        matrix[size - 1][size - 1].setText(""); // tạo 1 ô trống
        randomMatrix();
        puzzle.setResizable(false);
        puzzle.pack();//
    }

    public void randomMatrix() {
        Random rd = new Random();
        for (int i = 0; i < 1000; i++) {
            Point p = getPositionOfEmptyButton(); //tim vi tri cua rong
            int number = rd.nextInt(4);
            switch (number) {
                case 0: // tren
                    if (p.x > 0) { // hang dau tien --> khong the move len tren đc
                        matrix[p.x][p.y].setText(matrix[p.x - 1][p.y].getText());
                        matrix[p.x - 1][p.y].setText("");
                    }
                    break;
                case 1: // duoi
                    if (p.x < size - 1) {
                        matrix[p.x][p.y].setText(matrix[p.x + 1][p.y].getText());
                        matrix[p.x + 1][p.y].setText("");
                    }
                    break;
                case 2: // phai
                    if (p.y < size - 1) {
                        matrix[p.x][p.y].setText(matrix[p.x][p.y + 1].getText());
                        matrix[p.x][p.y + 1].setText("");
                    }
                    break;
                case 3: // trai
                    if (p.y > 0) {
                        matrix[p.x][p.y].setText(matrix[p.x][p.y - 1].getText());
                        matrix[p.x][p.y - 1].setText("");
                    }
                    break;
            }
        }
    }

    public boolean checkWin() {
        // point is empty must at last index of button
        if (!matrix[size - 1][size - 1].getText().equals("")) {
            return false;
        }
        // khi last button is empty
        int valueLastButton = size * size;
        int value = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                value++;
                if (value == valueLastButton) {
                    return true;
                }
                String btn = matrix[i][j].getText();
                // when last button is empty then get value from mul double size
//                if (btn.equals("")) {
//                     btn = size * size + "";// 9
//                }
                int number = Integer.parseInt(btn);
                // if last value button of matrix equal with value of  variable count all button 
                if (number != value) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkMove(JButton button) {
        if (button.getText().equals("")) { // khac empty
            return false;
        }
        //get Index is Empty
        Point p = getPositionOfEmptyButton();
        Point clickedPoint = null;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // get index Button when user clicked
                if (matrix[i][j].getText().equals(button.getText())) {
                    clickedPoint = new Point(i, j);
                }
            }
        }
        // khi muon move button thì ton tại 2 truong hợp
        //1: trùng p.x as row in matrix - p.y as col matrix more than 1 đơn vị
        //button empty vs button has value must be 
        if (p.x == clickedPoint.x && Math.abs(p.y - clickedPoint.y) == 1) {
            return true;
        }
        //1: trùng p.y as col in matrix - p.x as row matrix more than 1 đơn vị
        if (p.y == clickedPoint.y && Math.abs(p.x - clickedPoint.x) == 1) {
            return true;
        }
        return false;
    }

    public Point getPositionOfEmptyButton() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // find out index button is Empty
                if (matrix[i][j].getText().equals("")) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    public void moveButton(JButton button) {
        Point p = getPositionOfEmptyButton(); //lay vi tri cua button rong
        matrix[p.x][p.y].setText(button.getText());// button is empty will recives value of button move
        button.setText("");
        moveCount++;
        puzzle.lblCountMove.setText(String.valueOf(moveCount));
    }

    // 1 2 3 4 5 8 6 7 error --> ok
    // // 0=yes, 1=no, 2=cancel
    public void messPressNewGame(int i) {
        int confirm = JOptionPane.showConfirmDialog(null, "Do you must"
                + " be want to make new game?", "New Game", JOptionPane.YES_NO_OPTION);
        switch (confirm) {
            case 0: {
                this.runNewGame();
                break;
            }
            default: {
                if (i == 1) {
                    timer.start();
                }
                if (i == 0) {
                    timer.stop();
                }
            }
        }
    }

    public void newGame() {
        puzzle.getBtnNewGame().addActionListener((e) -> {
            if (isGameStart) { // true 
                //dang choi game --> 
                // user muốn new game thì timer  se dừng lại hiện thi ra dialog mess yes or no
                //isGameStart = false;
                timer.stop();
                messPressNewGame(1);
//                int confirm = JOptionPane.showConfirmDialog(null, "Do you must"
//                        + " be want to make new game?", "New Game", JOptionPane.YES_NO_OPTION);
//                switch (confirm) {
//                    case 0: {
//                        this.runNewGame();
//                        break;
//                    }
//                    case 1: {
//                        timer.start();
//                        break;
//                    }
//                    default: {
//                        timer.start();
//                    }
//                }
            } else {
                // khi mà chuong trình bắt đầu chạy isGameStart = false ;
                //  tạo 1 game mới để chơi.
                //  game chưa chạy. isGameStart = false;
                try {
                    messPressNewGame(0);
                } catch (Exception ex) {

                }

//                timer.stop();
                // this.runNewGame();
            }
        });
    }
}
