package com.gson.core;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gson.model.Lesson;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ReadExample {
    public static void main(String[] args) {
        Gson gson = Converters.registerLocalTime(new GsonBuilder()).create();

        Type lessonListType = new TypeToken<List<Lesson>>() {}.getType();

        try (FileReader reader = new FileReader("lessons.json")) {
            List<Lesson> lessons = gson.fromJson(reader, lessonListType);
            System.out.println("Прочитано из JSON:");
            lessons.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
