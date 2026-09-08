package com.gson.core;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gson.model.Lesson;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class WriteExample {
    public static void main(String[] args) {
        Gson gson = Converters.registerLocalTime(new GsonBuilder()).setPrettyPrinting().create();

        Lesson lesson1 =
                new Lesson(
                        "Математика",
                        LocalTime.of(0, 45),
                        Arrays.asList("Дискриминант", "Логарифмы", "Теорема Пифагора"));
        Lesson lesson2 =
                new Lesson(
                        "Физика",
                        LocalTime.of(1, 30),
                        Arrays.asList("Кинематика", "Динамика", "Оптика"));
        Lesson lesson3 =
                new Lesson(
                        "Информатика",
                        LocalTime.of(0, 50),
                        Arrays.asList("Алгоритмы", "Структуры данных"));

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2, lesson3);

        String json = gson.toJson(lessons);
        System.out.println("Сгенерированный JSON:\n" + json);

        try (FileWriter writer = new FileWriter("lessons.json")) {
            writer.write(json);
            System.out.println("Записано в lessons.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
