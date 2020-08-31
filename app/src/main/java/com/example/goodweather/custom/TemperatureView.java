package com.example.goodweather.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.goodweather.R;

public class TemperatureView extends View {
    // Цвет внешнего градускника
    private int outerColor = Color.BLACK;
    // Цвет выше нуля
    private int plusColor = Color.RED;
    // Цвет ниже нуля
    private int minusColor = Color.BLUE;
    // Цвет нуля
    private int zeroColor = Color.GRAY;
    // Температура
    private Integer temperature = null;
    // Изображение внешнего градусника
    private RectF outerRectangle = new RectF();
    // Изображение внутреннего градусника
    private RectF innerRectangle = new RectF();
    // "Краска" внешнего градусника
    private Paint outerPaint;
    // "Краска" внутреннего градусника
    private Paint innerPaint;
    // Ширина элемента
    private int width = 0;
    // Высота элемента
    private int height = 0;

    // Константы
    // Отступ элементов
    private final static int padding = 10;
    // Скругление углов
    private final static int round = 5;
    // Отступ между внешним и внутренним градускником
    private final static int outerPadding = 10;
    //Максимальное значение температуры
    private final static double maximumTemperature = 40;


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
        outerColor = typedArray.getColor(R.styleable.TemperatureView_outer_color, Color.BLACK);
        plusColor = typedArray.getColor(R.styleable.TemperatureView_plus_color, Color.RED);

        // вторым параметром идет значение по умолчанию, если атрибут не указан в макете,
        // то будет подставлятся эначение по умолчанию.
        minusColor = typedArray.getColor(R.styleable.TemperatureView_minus_color, Color.BLUE);
        zeroColor = typedArray.getColor(R.styleable.TemperatureView_zero_color, Color.GRAY);

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
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeWidth(5);
        initInnerRectangle();
    }

    private void initInnerRectangle() {
        innerPaint = new Paint();
        if (temperature == null || temperature == 0) {
            innerPaint.setColor(zeroColor);
        } else if (temperature > 0) {
            innerPaint.setColor(plusColor);
        } else {
            innerPaint.setColor(minusColor);
        }
        innerPaint.setStyle(Paint.Style.FILL);

        if (temperature != null) {
            int right =  (int) ((width - padding - outerPadding) * ((double) (Math.abs(temperature)/maximumTemperature)));

            innerRectangle.set(padding + outerPadding,
                    padding + outerPadding,
                    right,
                    height - padding - outerPadding);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Получить реальные ширину и высоту
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();
        // Отрисовка
        outerRectangle.set(padding,
                padding,
                width - padding,
                height - padding);

        initInnerRectangle();
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(outerRectangle, round, round, outerPaint);
        canvas.drawRoundRect(innerRectangle, round, round, innerPaint);
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
        initInnerRectangle();
        invalidate();
    }

    public void setTemperature(String temperature) {
        if (temperature.equals(getResources().getString(R.string.default_temperature))) {
            this.temperature = null;
        } else {
            this.temperature = Integer.parseInt(temperature);
        }
        initInnerRectangle();
        invalidate();
    }
}
