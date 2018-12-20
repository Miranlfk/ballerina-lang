import ballerina/grpc;
import ballerina/io;

// Generated non-blocking client endpoint based on the service definition.
public type HelloWorldClient client object {
    private grpc:Client grpcClient = new;
    private grpc:ClientEndpointConfig config = {};
    private string url;

    function __init(string url, grpc:ClientEndpointConfig? config = ()) {
        self.config = config ?: {};
        self.url = url;
        // Initialize client endpoint.
        grpc:Client c = new;
        c.init(self.url, self.config);
        error? result = c.initStub("non-blocking", ROOT_DESCRIPTOR,
                                                            getDescriptorMap());
        if (result is error) {
            panic result;
        } else {
            self.grpcClient = c;
        }
    }


    remote function lotsOfReplies(string req, service msgListener,
                                  grpc:Headers? headers = ()) returns (error?) {
        return self.grpcClient->nonBlockingExecute("HelloWorld/lotsOfReplies",
                                            req, msgListener, headers = headers);
    }

};


const string ROOT_DESCRIPTOR = "0A1B677270635F7365727665725F73747265616D696E672E70726F746F1A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F325B0A0A48656C6C6F576F726C64124D0A0D6C6F74734F665265706C696573121C2E676F6F676C652E70726F746F6275662E537472696E6756616C75651A1C2E676F6F676C652E70726F746F6275662E537472696E6756616C75653001620670726F746F33";
function getDescriptorMap() returns map<string> {
    return {
        "grpc_server_streaming.proto":
        "0A1B677270635F7365727665725F73747265616D696E672E70726F746F1A1E676F6F676"
        + "C652F70726F746F6275662F77726170706572732E70726F746F325B0A0A48656C6C6F"
        + "576F726C64124D0A0D6C6F74734F665265706C696573121C2E676F6F676C652E70726"
        + "F746F6275662E537472696E6756616C75651A1C2E676F6F676C652E70726F746F6275"
        + "662E537472696E6756616C75653001620670726F746F33",
        "google/protobuf/wrappers.proto":
        "0A1E676F6F676C652F70726F746F6275662F77726170706572732E70726F746F120F676"
        + "F6F676C652E70726F746F62756622230A0B446F75626C6556616C756512140A057661"
        + "6C7565180120012801520576616C756522220A0A466C6F617456616C756512140A057"
        + "6616C7565180120012802520576616C756522220A0A496E74363456616C756512140A"
        + "0576616C7565180120012803520576616C756522230A0B55496E74363456616C75651"
        + "2140A0576616C7565180120012804520576616C756522220A0A496E74333256616C75"
        + "6512140A0576616C7565180120012805520576616C756522230A0B55496E743332566"
        + "16C756512140A0576616C756518012001280D520576616C756522210A09426F6F6C56"
        + "616C756512140A0576616C7565180120012808520576616C756522230A0B537472696"
        + "E6756616C756512140A0576616C7565180120012809520576616C756522220A0A4279"
        + "74657356616C756512140A0576616C756518012001280C520576616C756542570A136"
        + "36F6D2E676F6F676C652E70726F746F627566420D577261707065727350726F746F50"
        + "015A057479706573F80101A20203475042AA021E476F6F676C652E50726F746F62756"
        + "62E57656C6C4B6E6F776E5479706573620670726F746F33"

    };
}
