package com.gson.core.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gson.model.Lesson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GsonTest {

    @TempDir Path tempDir;

    @Test
    void shouldWriteAndReadLessons() throws Exception {
        File json = tempDir.resolve("lessons.json").toFile();

        Lesson l1 = new Lesson("Math", LocalTime.of(1, 0), List.of("A", "B"));
        Lesson l2 = new Lesson("Physics", LocalTime.of(0, 30), List.of("C"));
        List<Lesson> lessons = List.of(l1, l2);

        Gson gson = Converters.registerLocalTime(new GsonBuilder()).setPrettyPrinting().create();

        try (FileWriter w = new FileWriter(json)) {
            gson.toJson(lessons, w);
        }

        Type type = new TypeToken<List<Lesson>>() {}.getType();
        List<Lesson> read;
        try (FileReader r = new FileReader(json)) {
            read = gson.fromJson(r, type);
        }

        assertThat(read).hasSize(2);
        assertThat(read.get(0).getSubject()).isEqualTo("Math");
        assertThat(read.get(0).getDuration()).isEqualTo(LocalTime.of(1, 0));
        assertThat(read.get(1).getTopics()).containsExactly("C");
    }

    @Test
    void shouldThrowIOExceptionWhenFileCannotBeRead() {
        File invalid = new File("/nonexistent/path/file.json");

        var err = new java.io.ByteArrayOutputStream();
        System.setErr(new java.io.PrintStream(err));
        com.gson.core.ReadExample.main(
                new String[0]);
    }
}
