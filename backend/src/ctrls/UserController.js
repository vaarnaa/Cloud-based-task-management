const firebase = require('../db');
// const ref = firebase.database().ref("/Users");

const UserController = {

  getAll(req, res) {
    console.log("UserController.getAll");

    const users = firebase.database().ref().child('users');
    res.status(200).json({ status: 'ok', data: users });
  },

  createUser(req, res) {
    console.log("UserController.createUser");

    const newUserId = firebase.database().ref().child('users').push().key;
    console.log(`new user id: ${newUserId}`);

    const updates = {};
    updates['/users/' + newUserId] = req.body;
    console.log(`updating with: ${JSON.stringify(updates)}`);

    firebase.database().ref().update(updates)
      .then(values => {
        console.log('UserController.getAll: ok');
        res.status(201).json({ status: 'ok', data: values });
      })
      .catch(() => {
        console.log('UserController.getAll: error');
        res.status(500).json({ status: 'internal error' });
      });
  },

  // updateSingle(req, res) {
  //   console.log("HTTP POST Request");
  //   var userName = req.body.UserName;
  //   var name = req.body.Name;
  //   var age = req.body.Age;

  //   var userReference = firebase.database().ref(`/Users/${userName}/`);
  //   userReference.update({Name: name, Age: age},
  //       function(error) {
  //           if (error) {
  //               res.send("Data could not be updated." + error);
  //           }
  //           else {
  //               res.send("Data updated successfully.");
  //           }
  //       }
  //   );
  // }

  // removeSingle(req, res) {
  //   console.log("HTTP DELETE Request");
  // }


};

module.exports = UserController;
