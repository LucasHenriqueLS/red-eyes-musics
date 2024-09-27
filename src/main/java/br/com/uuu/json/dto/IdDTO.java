package br.com.uuu.json.dto;

public record IdDTO<T> (T id) implements Identifiable<T> { }
