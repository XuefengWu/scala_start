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

    className: "well",

    render:function (eventName) {
        this.questionView = new QuestionView({model:this.model});
        $(this.el).html(this.questionView.render().el);
        return this;
    }

});

window.QuestionView = Backbone.View.extend({

    template:_.template($('#tpl-question-item').html()),

    render:function (eventName) {
        this.choiceListView = new ChoiceListView({model:this.model.toJSON().choices});
        $(this.el).html(this.template(this.model.toJSON())).append(this.choiceListView.render().el);
        return this;
    },

 close:function () {
        $(this.el).unbind();
        $(this.el).empty();
    }
});

window.ChoiceListView = Backbone.View.extend({

    tagName: "table",

    render: function(){
      _.each(this.model.models, function (choice) {
          $(this.el).append(new ChoiceListItemView({model:choice}).render().el);
      }, this);
      return this;
    }

});


window.ChoiceListItemView = Backbone.View.extend({

    tagName:"tr",

    template:_.template($('#tpl-question-choice').html()),

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

        if(this.model.toJSON().correct){
            console.log(this.model.toJSON().title+" :Bingo!");
            $('#i-'+target.id).attr("class","icon-ok");
        }else{
            $('#i-'+target.id).attr("class","icon-remove");
        }
        // You could change your model on the spot, like this:
        // var change = {};
        // change[target.name] = target.value;
        // this.model.set(change);
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