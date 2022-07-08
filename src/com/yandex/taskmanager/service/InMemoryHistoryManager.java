package com.yandex.taskmanager.service;

import com.yandex.taskmanager.model.Task;
import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    protected final MemoryLinkedList history = new MemoryLinkedList();

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }


    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void removeById(int id) {
        history.removeNodeById(id);
    }

    @Override
    public void removeAll(Map<Integer, ? extends Task> map) {
        history.removeNodeAll(map);
    }
}


class MemoryLinkedList {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> historyToEdit = new HashMap<>();


    public void linkLast(Task element) {
        removeNodeById(element.getId());
        final Node oldTail = tail;
        final Node newNode = new Node(element, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        historyToEdit.put(element.getId(), newNode);
    }

    public ArrayList<Task> getTasks() {
        final ArrayList<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.getData());
            node = node.getNext();
        }
        return tasks;
    }

    public void removeNodeById(int idNode) {
        if (historyToEdit.containsKey(idNode)) {
            Node element = historyToEdit.get(idNode);
            historyToEdit.remove(idNode);
            final Node prev = element.getPrev();
            final Node next = element.getNext();
            if (prev == null && next != null) {
                next.setPrev(null);
                head = next;
            } else if (next == null && prev != null) {
                prev.setNext(null);
                tail = prev;
            } else if (next == null) {
                head = null;
                tail = null;
            } else {
                prev.setNext(next);
                next.setPrev(prev);
            }
        }
    }

    public void removeNodeAll(Map<Integer, ? extends Task> map) {
        for (Integer id : map.keySet()) {
            if (historyToEdit.containsKey(id)) {
                removeNodeById(id);
            }
        }

    }


}

class Node {
    private final Task data;
    private Node next;
    private Node prev;

    public Node(Task data, Node next, Node prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, next, prev);
    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }


    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}