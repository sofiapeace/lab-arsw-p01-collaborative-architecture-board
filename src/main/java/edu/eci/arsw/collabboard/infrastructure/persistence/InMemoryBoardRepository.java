package edu.eci.arsw.collabboard.infrastructure.persistence;

import edu.eci.arsw.collabboard.application.port.out.BoardRepository;
import edu.eci.arsw.collabboard.domain.model.Board;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryBoardRepository implements BoardRepository {

    /*
     * Intentionally simple for Lab 04.
     * Thread-safety is NOT the focus of this lab. Do not redesign this yet only
     * because you remember concurrency from previous weeks; that concern will
     * return in a later evolution of the same application.
     */
    private final Map<String, Board> boards = new HashMap<>();

    @Override
    public Board save(Board board) {
        // Semantics: upsert. put() overwrites unconditionally whether the id
        // already existed or not. This repository does not decide whether an
        // overwrite is "allowed" — that business rule (a replace must target
        // an existing board) belongs to BoardApplicationService, which checks
        // existsById() before calling save() for a replace.
        boards.put(board.id(), board);
        return board;
    }

    @Override
    public Optional<Board> findById(String boardId) {
        // No defensive copy needed here: Board is an immutable record whose
        // compact constructor already does List.copyOf(elements), and it has
        // no mutator methods. The reference handed back cannot be used to
        // corrupt what is stored in this map.
        return Optional.ofNullable(boards.get(boardId));
    }

    @Override
    public boolean existsById(String boardId) {
        return boards.containsKey(boardId);
    }
}