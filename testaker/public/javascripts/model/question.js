window.Choice = Backbone.Model.extend();

// children collection
var Choices = Backbone.Collection.extend({
    model: Choice
});

window.Question = Backbone.Model.extend({
    initialize: function() {
        if (Array.isArray(this.get('choices'))) {
            this.set({choices: new Choices(this.get('choices'))});
        }
    }
});



window.QuestionCollection = Backbone.Collection.extend({
    model:Question,
    url:"/api/q"
});

