package com.github.nekolr.slime.model;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.annotation.Return;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Grammar {

    private String owner;

    private String method;

    private String comment;

    private String example;

    private String function;

    private List<String> returns;

    public static List<Grammar> findGrammars(Class<?> clazz, String function, String owner, boolean mustStatic) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Grammar> grammars = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && (Modifier.isStatic(method.getModifiers()) || !mustStatic)) {
                Grammar grammar = new Grammar();
                grammar.setMethod(method.getName());
                Comment comment = method.getAnnotation(Comment.class);
                if (comment != null) {
                    grammar.setComment(comment.value());
                }
                Example example = method.getAnnotation(Example.class);
                if (example != null) {
                    grammar.setExample(example.value());
                }
                Return returns = method.getAnnotation(Return.class);
                if (returns != null) {
                    Class<?>[] classes = returns.value();
                    List<String> returnTypes = new ArrayList<>();
                    for (int i = 0; i < classes.length; i++) {
                        returnTypes.add(classes[i].getSimpleName());
                    }
                    grammar.setReturns(returnTypes);
                } else {
                    grammar.setReturns(Collections.singletonList(method.getReturnType().getSimpleName()));
                }
                grammar.setFunction(function);
                grammar.setOwner(owner);
                grammars.add(grammar);
            }
        }
        return grammars;
    }
}
