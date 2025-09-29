package org.example.records;

/// Represents a song record with attributes for the artist, release year, album, and title.
/// This record is immutable and can be used to encapsulate metadata information about a song.
/// Commonly used in scenarios where song metadata needs to be processed, stored, or displayed.
/// The following fields are included:
/// - artist: The name of the artist or band who performed the song.
/// - year: The release year of the song.
/// - album: The album name where the song appears.
/// - title: The title of the song.
public record SongRecord(String artist, String year, String album, String title) {
}
