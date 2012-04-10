
window.Question = Backbone.Model.extend();

window.QuestionCollection = Backbone.Collection.extend({
    model:Question,
    url:"/api/q"
});


window.QuestionListView = Backbone.View.extend({
    tagName:'ul',
    initialize:function () {
            this.model.bind("reset", this.render, this);
    },
    render:function (eventName) {
           _.each(this.model.models, function (question) {
               $(this.el).append(new QuestionListItemView({model:question}).render().el);
           }, this);
           return this;
    }
});

window.QuestionListItemView = Backbone.View.extend({

    tagName:"li",

    template:_.template($('#tpl-question-list-item').html()),

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }

});

window.QuestionView = Backbone.View.extend({

    template:_.template($('#tpl-question-details').html()),

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },

    events:{
        "change input":"change",
        "change textarea":"change",
    },

  change:function (event) {
        var target = event.target;
        console.log('changing ' + target.id + ' from: ' + target.defaultValue + ' to: ' + target.value);
        // You could change your model on the spot, like this:
        // var change = {};
        // change[target.name] = target.value;
        // this.model.set(change);
    },

 close:function () {
        $(this.el).unbind();
        $(this.el).empty();
    }
});


var AppRouter = Backbone.Router.extend({

    routes:{
        "":"list",
        "q/:id":"questionDetails"
    },

    list:function () {
        this.questionList = new QuestionCollection();
        this.questionListView = new QuestionListView({model:this.questionList});
        this.questionList.fetch();
        $('#sidebar').html(this.questionListView.render().el);
    },

    questionDetails:function (id) {
        this.question = this.questionList.get(id);
        this.questionView = new QuestionView({model:this.question});
        $('#content').html(this.questionView.render().el);
    }
});

var app = new AppRouter();
Backbone.history.start();