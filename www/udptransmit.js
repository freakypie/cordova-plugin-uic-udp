// Start of cut and paste area (to put back in Git repo version of this file)
module.exports = {

    // Order of parameters on all calls:
    // - callback function
    // - error function
    // - native class name
    // - native method name
    // - arguments for method
    
    sendto : function(message, host, port, err) {
        if (!err) {
            err = function() {
            };
        }
        cordova.exec(function() {
            console.log("sent", message, "to", host, port);
        }, err, "UDPTransmit", "sendto", [ message, host, port ]);
        return true;
    },
    recvfrom : function(success, err) {
        if (!err) {
            err = function() {
            };
        }
        cordova.exec(success, err, "UDPTransmit", "recvfrom", []);
        return true;
    }

};
// End of cut and paste area
