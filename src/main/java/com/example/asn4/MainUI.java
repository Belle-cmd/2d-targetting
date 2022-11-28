package com.example.asn4;

import javafx.scene.layout.StackPane;

public class MainUI extends StackPane {

    public MainUI() {
        // create mvc components
        BlobModel model = new BlobModel();
        BlobController controller = new BlobController();
        BlobView view = new BlobView(1080);  // where canvas is created
        view.setMaxSize(Double.MAX_VALUE, 1080);
        InteractionModel iModel = new InteractionModel();  // keeps track of the selected blob


        // connect mvc components
        controller.setModel(model);
        view.setModel(model);
        controller.setIModel(iModel);
        view.setIModel(iModel);
        model.addSubscriber(view);
        iModel.addBlobSubscriber(view);
        iModel.addSelectionSubscriber(view);  // enables selection to be drawn in canvas
        view.setController(controller);

        this.getChildren().add(view);
    }
}
