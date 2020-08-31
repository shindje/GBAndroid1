package com.example.goodweather.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.goodweather.R;

public class TemperatureView extends View {
    // Цвет внешнего градускника
    private int outerColor = getResources().getColor(R.color.colorPrimary);
    // Цвет выше нуля
    private int plusColor = Color.RED;
    // Цвет ниже нуля
    private int minusColor = Color.CYAN;
    // Температура
    private Integer temperature = null;
    // Изображение внешнего градусника
    Path outerPath = new Path();
    // Изображение внутреннего градусника
    Path innerPath = new Path();
    // "Краска" внешнего градусника
    private Paint outerPaint;
    // "Краска" внутреннего градусника
    private Paint innerPaint;
    // Ширина элемента
    private int width;
    // Высота элемента
    private int height;
    // Толщина градусника
    private static int thickness;
    // Радиус внешнего градусника
    private static int radius;

    // Константы
    // Отступ элементов
    private final static int padding = 5;
    // Максимальное значение температуры
    private final static double maximumTemperature = 40;
    // Толщина показателя температуры
    private final static float innerThickness = 6f;


    public TemperatureView(Context context) {
        super(context);
        init();
    }

    // Вызывается при добавлении элемента в макет
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    public TemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    // int defStyleRes - ресурс стиля, если он не был определен в предыдущем параметре
    public TemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    // Инициализация атрибутов пользовательского элемента из xml
    private void initAttr(Context context, AttributeSet attrs) {

        // При помощи этого метода получаем доступ к набору атрибутов.
        // На выходе массив со значениями
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemperatureView, 0,
                0);

        // Чтобы получить какое-либо значение из этого массива,
        // надо вызвать соответсвующий метод, и передав в этот метод имя ресурса
        // указанного в файле определения атрибутов (attrs.xml)
        outerColor = typedArray.getColor(R.styleable.TemperatureView_outer_color,  getResources().getColor(R.color.colorPrimary));
        plusColor = typedArray.getColor(R.styleable.TemperatureView_plus_color, Color.RED);

        // вторым параметром идет значение по умолчанию, если атрибут не указан в макете,
        // то будет подставлятся эначение по умолчанию.
        minusColor = typedArray.getColor(R.styleable.TemperatureView_minus_color, Color.CYAN);

        if (typedArray.hasValue(R.styleable.TemperatureView_temperature))
            temperature = typedArray.getInteger(R.styleable.TemperatureView_temperature, 0);

        // В конце работы дадим сигнал,
        // что нам больше массив со значениями атрибутов не нужен
        // Система в дальнейшем будет переиспользовать этот объект,
        // и мы никогда не получим к нему доступ из этого элемента
        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    private void init(){
        outerPaint = new Paint();
        outerPaint.setColor(outerColor);
        outerPaint.setStyle(Paint.Style.FILL);
        initInnerShape();
    }

    private void initInnerShape() {
        innerPaint = new Paint();
        if (temperature != null) {
            if (temperature > 0) {
                innerPaint.setColor(plusColor);
            } else {
                innerPaint.setColor(minusColor);
            }
        }
        innerPaint.setStyle(Paint.Style.FILL);

        if (temperature != null) {
            int right =  (int) ((width - padding - thickness) * ((double) (Math.abs(temperature)/maximumTemperature)));

            innerPath = new Path();
            int innerRadius = (height - padding*2)/4;
            innerPath.addCircle(padding + radius, height/2.0f, innerRadius, Path.Direction.CW);
            innerPath.addRect(padding + radius, height/2.0f - innerThickness/2.0f,
                    right, height/2.0f + innerThickness/2.0f,
                    Path.Direction.CW);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Получить реальные ширину и высоту
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        thickness = height - padding*6;
        radius = (height - padding*2)/2;

        // Отрисовка
        outerPath = new Path();
        outerPath.addCircle(padding + radius, height/2.0f, radius, Path.Direction.CW);
        outerPath.addRect(padding + radius, height/2.0f - thickness/2.0f,
                width - padding - radius/2.0f, height/2.0f + thickness/2.0f,
                Path.Direction.CW);
        outerPath.addCircle(width - padding - thickness/2.0f, height/2.0f,
                thickness/2.0f, Path.Direction.CW);

        // Линии отметок
        float top = height/2.0f + thickness/2.0f;
        float dy = height - padding - top;
        float x0 = padding + radius*2.0f;
        float x1 = (width - padding - thickness/2.0f);
        float dx = (x1-x0)/4.0f;
        for (int i = 0; i < 5; i++) {
            float bottom = top + dy * (2 - i%2);
            outerPath.addRect(x0 + dx*i - 2, top,  x0 + dx*i + 2, bottom, Path.Direction.CCW);
        }

        initInnerShape();
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(outerPath, outerPaint);
        canvas.drawPath(innerPath, innerPaint);

//        canvas.drawRoundRect(innerRectangle, round, round, innerPaint);
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
        initInnerShape();
        invalidate();
    }

    public void setTemperature(String temperature) {
        if (temperature.equals(getResources().getString(R.string.default_temperature))) {
            this.temperature = null;
        } else {
            this.temperature = Integer.parseInt(temperature);
        }
        initInnerShape();
        invalidate();
    }
}
