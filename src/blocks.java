
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;

/**
 * shmonodrom entertaiment presents
 */
public class blocks extends JFrame {
    private int xMax = 800, yMax = 600, yMin = 0; //  Размеры листа
    private int[][] rects = {   // Фигуры: верхний массив - длина по Х, нижний по Y
            {75, 50, 60, 305, 85, 170, 225, 95, 85, 130, 100, 100, 200, 200, 175, 225, 110, 150, 180},
            {80, 90, 50, 300, 115, 350, 310, 110, 120, 100, 100, 100, 200, 200, 100, 200, 120, 125, 80}
    };
    private int denX = takeGreatestDenominator(rects[0]), denY = takeGreatestDenominator(rects[1]); //  Делители
    private boolean[][] isFilled = new boolean[xMax + 3 * denX][yMax + 3 * denY]; //    Булево поле
    private boolean isOver = false; //  Переменная конца работы основного цикла
    private ArrayList<Integer> minYcollection = new ArrayList<>();  //Коллекция минимальных Y

//    Конструктор, тут я разместил настройку окошка рисования, конструкция спешл фо Java
    private blocks(String s) {
        super(s);
        setLayout(null);
        setSize(xMax, yMax);
        setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

//  Полный список действий алгоритма:
//    1. Ищем ближайший свободный Х по минимальному Y (переменная x1)
//      1.1 При отсутствии берем следующий минимальный Y из коллекции (реализовано в методе findX)
//    2. Ищем ближайший занятый Х от ближайшего свободного по тому же самому У (переменная x2)
//    3. Проверяем на вместимость по Х самой маленькой фигуры
//      3.1 В случае провала возвращаемся на п. 1
//    4. Процесс поиска ближайшей наибольшей вставки начинается с самой большой фигуры в отсортированном массиве:
//      4.1 Проверяем ее по Х
//      4.2 При провале 4.1 примеряем Y фигуры на Х свободного места
//      4.3 При успехе 4.2 поворачиваем (метод Rotate)
//      4.4 При провале 4.1-4.3 берем следующую фигуру из массива и повторяем
//    5. Ищем ближайший У от минимального
//    6. Проверяем на вместимость по Y
//    7. Отрисовываем:
//      7.1 Наносим графику на холст
//      7.2 Биндим булы в isFilled как true
//      7.3 Добавляем Y в коллекцию
//      7.4 Удаляем фигуру из массива
//    P. S. Алгоритм заканчивает работу когда заканчиваются фигуры или когда минимальный У достигает максимального,
//    или фигуры перестают влазить в оставшиеся пространства
//    P. P. S. Но это в теории
    public void paint(Graphics myPicture) {
        int x1, x2, index = -1;
        initIsFilled();
        myPicture.setColor(Color.WHITE);
        myPicture.fillRect(0, 0, xMax, yMax);
        rects = sort(rects);        // TODO добавить другие варианты сортировки, например по группам с общим Y
        while (!isOver&& rects[0].length > 0){
            x1 = findX(0, yMin, false, false); // minX=0, yMin, isForFindY=false, isInvert=false
            x2 = findX(x1, yMin, false, true);
            if(rects[0].length != 0) {  // Есть ли что в массиве?
                if (x2 - x1 >= rects[0][rects[0].length - 1] &&
                   yMax >= yMin + rects[1][rects[1].length - 1]) { //Подходит ли самая маленькая в найденный участок
                    for (int i = 0; i < rects[0].length; i++){
                        if (x2 - x1 >= rects[0][i]) {   // Подходит ли очередная фигура по Х
                            index = i; //   Запоминаем номер
                        } else if (x2 - x1 >= rects[1][i]) { // Если по Х болт, пробуем по Y
                            rotate(i);  // Поворот фигуры
                            index = i;
                        }
                        if (index != -1 && compareY(yMin, index)){ // Влезает ли по Y
                            break;
                        } else index = -1;
                    }
                    if (index != -1) {
                        myPicture.setColor(Color.BLACK);
                        myPicture.fillRect(x1, yMin, rects[0][index], rects[1][index]);
                        myPicture.setColor(Color.RED);
                        myPicture.drawRect(x1, yMin, rects[0][index], rects[1][index]);
                        fillBoolField(x1, yMin, rects[0][index], rects[1][index]);  // заполнение булева поля
                        minYcollection.add(rects[1][index] + yMin);                 // добавление минимального Y
                        removeIndex(index);                                         // удаляем отрисованную фигуру
                        index = -1;                                                 // это для очередного прохода
                    }
                } else {
                    fillBoolField(x1, yMin, x2 - x1, denY);
                }
            }
        }
    }

//    Удаление фигуры по индексу из массива
    /*  0 1 2 3 4 5 6 - length
          1 2 3 4 5 6 - i
              3       - index
        0 1 2 4 5 6   - result
     */
    private void removeIndex(int index){
        int[] x = new int[rects[0].length - 1];
        int[] y = new int[rects[1].length - 1];
        for (int i = 1; i < rects[0].length; i++){
            if (i > index) {
                x[i - 1] = rects[0][i];
                y[i - 1] = rects[1][i];
            } else if (i <= index){
                x[i - 1] = rects[0][i - 1];
                y[i - 1] = rects[1][i - 1];
            }
        }
        if (x.length == 0 || y.length == 0) { //Условие конца работы цикла
            isOver = true;
        }
        rects[0] = x;
        rects[1] = y;
        for (int i : rects[0]) System.out.println(i);
    }

//    Проверка по Y, влезет ли фигура
    private boolean compareY (int y, int index) {
        return (yMax >= (y + rects[1][index]));
    }

//    Вычисление наибольшего общего делителя
//      Нужно для увеличения количества отсчитываемых за раз точек без потерь по точности
//      Как следствие, итоговое ускорение работы алгоритма при поисках Х и У
    private int takeGreatestDenominator(int massive[]) {
        int denominator = 1, sum = 0;
        List<Integer> denominatorList = new ArrayList<>();
        for (int j = 1; j < 10; j++) {
            for (int aMassive : massive) {
                sum = sum + aMassive % j;
            }
            if (sum == 0) {
                    denominatorList.add(j);
            }
            sum = 0;
        }
        if (denominatorList.size() > 1) {
            denominator = denominatorList.get(denominatorList.size() - 1);
        }
//        Перемножаем два последних множителя
        int d = denominatorList.get(denominatorList.size() - 1) * denominatorList.get(denominatorList.size() - 2);
        for (int aMassive : massive) {
            sum = sum + aMassive % d;
        }
        if (sum == 0) {
            denominator = d;
        }
        return denominator;
    }

//    Поиск ближайшего свободного X по линии minY через булево поле
    private int findX (int minX, int minY, boolean isForFindY, boolean isInvert) {
        int x = -1, i = minX;
        while (i < xMax + denX && !isOver) {
            if (isFilled[i][minY] == isInvert) { // isInvert = true ищет занятые Х вместо свободных
                x = i;
                yMin = minY;
                break;
            }
            i = i + denX;
        }
        if (x == -1 && !isForFindY &&!isOver) { // isForFindY - для исключения рекурсии при вызове из findMinY
            x = findX(0, findMinY(minY), false, isInvert);
        }
        return x;
    }

//    Поиск по Y
    private int findMinY (int minY) { //TODO - пофиксить yMin
        int y = 0;
        if (!minYcollection.isEmpty()) { //   Проверяем, есть ли что-то в коллекции
            minYcollection.sort(Comparator.naturalOrder());        //   Сортировка по возрастанию
            y = minYcollection.get(0);  //  Берем самый маленький
            minYcollection.remove(0);   //  Удаляем, чтобы не повторяться
        } else {                              // Если в массиве мышь откинулась, включаем ручной поиск через булево поле
            int i = minY;
            while(i < yMax){
                i = i + denY;
                if (findX(0, i, true, false) != -1) {
                    y = i;
                    break;
                }
                if (i == 600) {  // хз работает ли
                    isOver = true;
                    break;
                }
            }
        }
        if (y == yMax) isOver = true; // Конец работы основного цикла, если лист закончился
        yMin = y;
        return y;
    }

//    Заполнение булева поля, try - catch это чисто жаба конструкция для исключений
    private void fillBoolField (int startX, int startY, int lengthX, int lengthY){
            for (int i = startX; i < startX + lengthX; i++) {
                for (int j = startY; j < startY + lengthY; j++) {
                    isFilled[i][j] = true;
                }
            }
    }

//    Поворот фигуры (взаимообратная замена Х и У)
    private void rotate (int index) {
        int x = rects[0][index];
        int y = rects[1][index];
        rects[0][index] = y;
        rects[1][index] = x;
    }

//    Инициализация булева поля
    private void initIsFilled () {
        for (int i = 0; i < xMax + denX; i ++) {
            for (int j = 0; j < yMax + denY; j++){
                isFilled[i][j] = i >= xMax || j >= yMax;
            }
        }
    }

//    Сортировка массива блоков по убыванию, начиная с большего - TRUTH
    private int[][] sort(int[][] rect) {
        int x1, y1;
        for (int i = 0; i < rect[0].length; i++) {
            for (int a = i; a > 0; a--) {
                if ((rect[0][a] * rect[1][a]) > (rect[0][a - 1] * rect[1][a - 1])) {
                    x1 = rect[0][a];
                    y1 = rect[1][a];
                    rect[0][a] = rect[0][a - 1];
                    rect[1][a] = rect[1][a - 1];
                    rect[0][a - 1] = x1;
                    rect[1][a - 1] = y1;
                }
            }
        }
        return rect;
    }

//    Старт алгоритма, инструмент теста - TRUTH
        public static void main (String args[]){
            new blocks("");
        }

}