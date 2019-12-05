module.exports = {
    'ProjectController': require('./ProjectController'),
    'TaskController': require('./TaskController'),
}

// Notes:

// firebase.database().ref() -> root
// firebase.database().ref().child('users') -> list of users
// firebase.database().ref('/users/id') -> specific object

// ref.push(obj);   // Creates a new ref with a new "push key"
// ref.set(obj);    // Overwrites the path
// ref.update(obj); // Updates only the specified attributes
