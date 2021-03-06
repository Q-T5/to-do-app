package npc.martin.todoapp.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import npc.martin.todoapp.model.TodoList;
import npc.martin.todoapp.model.TodoObject;
import npc.martin.todoapp.model.PersistenceTransactions;

/**
 *
 * @author bikathi_martin
 * <h4>The FindAndEditTodo Class</h4>
 * <p>This class has methods to help find and edit todos we have already saved.</p>
 */
public class FindAndEditTodo extends CreateTodo {
    //these two variables represent the position in the list the todo occupies, and
    //helper methods to perform transactions to and from the storage files
    private Integer positionInList = null;
    PersistenceTransactions transaction = new PersistenceTransactions();
    private List<TodoObject> markedDone = new ArrayList<>();
    
    /**
     * <h3>findPsecTodo()</h3>
     * <p>works when you're not sure if the ID you're providing (you're 'speculating') exists.
     * It's called directly by the user of the program
     * </p>
     * @param targetTodoId 
     */
    public void findSpecTodo(String targetTodoId) {
        //first we load the lists to search from
        listActions = transaction.readSavedJSON();
        markedDone = transactions.readMarkedDone();
        
        //then we loop through the list
        //we compare your provided id to that of each item in the list
        for(TodoObject todoItem : listActions.getTodoList()) {
            String todoItemId = todoItem.getTodoId();
            //if we find an id that matches
            if(todoItemId.equals(targetTodoId)) {
               //we get its position in the list and generate a table to show you what we found
               //the table has information linked to that id
               positionInList = listActions.getTodoList().indexOf(todoItem);
               System.out.println("Match found! Populating results table... ");
               new GenerateTodoTables().generateWithIndex(positionInList, listActions);
               
               //we break out of the loop when we find a match
               break;
               
            //else if we don't have a match after looping through the entire first list, we print out a notification
            //the start looking in the second list
            } else if((listActions.getTodoList().indexOf(todoItem) == listActions.getTodoList().size() - 1)) {
                System.out.print("Sorry, no match for that ID :( \nSearching in the list of done todos...");
                for(TodoObject itemMarkedDone : markedDone) {
                    String itemId = itemMarkedDone.getTodoId();
                    //if we find an id that matches
                    if(itemId.equals(targetTodoId)) {
                        //we get its position in the list and generate a table to show you what we found
                       //the table has information linked to that id
                       positionInList = markedDone.indexOf(itemMarkedDone);
                       System.out.println("Match found! Populating results table... ");
                       
                       //because the available method to generate a table requires a TodoList object, we will
                       //have to build one on the fly and pass it to the method
                       TodoList temp = new TodoList();
                       temp.addTodo(markedDone.get(positionInList));
                       
                       //we pass in '0' because the index of the item in the temp object's list
                       //will always be 0
                       new GenerateTodoTables().generateWithIndex(0, temp);
                       
                       //we break out of the loop when we find a match
                       break;
                       
                       //but if we have come to the end of the second list
                       //then we print a message saying the item has not been found on both lists
                    } else if(markedDone.indexOf(itemMarkedDone) == markedDone.size() - 1) {
                       System.out.print("Sorry, no match for that ID in both lists :(");
                    }
                }
            }
        }
    }
    
    /**
     * <h3>findTodo(String targetTodoId, TodoList list)</h3>
     * <p>works when you know that the ID we're providing actually exists.
     * It can only be called by other methods.
     * </p>
     * @param targetTodoId
     * @param list
     * @return Integer
     */
    public Integer findTodo(String targetTodoId, TodoList list) {
        //we will take that id and compare it to others from a list that's given as an argument to the method
        for(TodoObject todoItem : list.getTodoList()) {
            String todoItemId = todoItem.getTodoId();
            
            //if we find a match, we take note of the list position where the match was found and break the loop
            if(todoItemId.equals(targetTodoId)) {
                positionInList = list.getTodoList().indexOf(todoItem);
                break;
            }
        }
        
        //that position is our return type
        return positionInList;
    }
    
    /**
     * <h3>editTodo(String targetTodoId, TodoList list)</h3>
     * <p>This method helps edit an already saved todo object. It works like the interactive mode
     * when creating a todo, except this time we're overwriting existing data with new data.
     * </p>
     * @param targetTodoId 
     */
    public void editTodo(String targetTodoId) {
        listActions = transaction.readSavedJSON();
        
        //we have to comfirm that the object we're editting actually exists
        Integer itemToEdit = this.findTodo(targetTodoId, listActions);
        
        //we only proceed to editing if it does exist
        if(itemToEdit != null) {
            System.out.println("Starting editor in interractive mode... ");
        
            System.out.print("Enter new simple todo definition[3 words max]: ");
            this.todoDefinition = s1.nextLine();
            this.listActions.todoList.get(itemToEdit).setTodoDefinition(todoDefinition);

            System.out.print("Enter new indepth todo definition[upto a paragraph]: ");
            this.todoDetails = s1.nextLine();
            this.listActions.todoList.get(itemToEdit).setTodoDetails(todoDetails);

            System.out.print("Enter new date to execute todo[Format: dd MM yyyy e.g 21 Mar 2021]: ");
            this.date = s1.nextLine();
            CharSequence dateAsCharSequence = this.date;
            this.dateToExecute = LocalDate.parse(dateAsCharSequence, DateTimeFormatter.ofPattern("dd MMM yyyy"));
            this.listActions.todoList.get(itemToEdit).setDateToExecute(dateToExecute);

            System.out.println("Viable parameters have been editted successfully :)");
            transactions.saveAsJSON(listActions);
            
        //otherwise we display a message saying the object does not exist
        } else {
            System.out.println("Sorry. No match found for that ID.");
        }
    }
}
