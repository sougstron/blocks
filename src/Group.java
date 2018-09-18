public class Group {
    private int index = 0, count;  // один индекс на экземпляр группы
    private int[][] members = new int[count][count];  //  один массив на экземпляр группы

    // Конструктор, в котором объявляется количество фигур TODO сделать резиновый объект
    public Group (int count) {
        this.count = count;
    }

    // Добавление фигуры
    public void addMember (int x, int y) {
        members[0][index] = x;
        members[1][index] = y;
        index++;
    }

    // Геттер
    public int[] getNextMember () { //TODO: можно ли передавать более этично, чем [100, 200] [0, 1] [0][0] [1][0]?
        int [] get = new int[2];
        get[0] = members[0][index - 1];
        get[1] = members[1][index - 1];
        return get;
    }

    // Выдача максимальной координаты
    public int getMaxValue (int coord) { //coord - x(0) or y(1)
        int max = 0;
        for (int i = 0; i < members[coord].length; i ++) {
            if (members[coord][i] > max) {
                max = members[coord][i];
            }
        }
        return max;
    }

    // Получить суммарное значение координат
    public int getSumValue (int coord) {
        int sum = 0;
        for (int i = 0; i < members[coord].length; i++){
            sum =+ members[coord][i];
        }
        return sum;
    }


    public boolean isIndexPos (){
        return index > 0;
    }
}
