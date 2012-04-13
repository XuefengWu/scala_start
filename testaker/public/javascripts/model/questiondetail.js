
window.ChoiceDetail = Backbone.Model.extend();

// children collection
var ChoiceDetails = Backbone.Collection.extend({
    model: ChoiceDetail
});

window.QuestionDetail = Backbone.Model.extend({

    urlRoot :"/api/q/",

    initialize: function() {
        if (Array.isArray(this.get('choices'))) {
            this.set({choices: new ChoiceDetail(this.get('choices'))});
        }
    }
});