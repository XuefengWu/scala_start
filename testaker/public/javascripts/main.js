/**  question ***/

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



window.ChoiceListItemView = Backbone.View.extend({

    tagName:"tr",

    template:_.template($('#tpl-question-choice').html()),

    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    },

    events:{
        "change input":"change",
        "change textarea":"change"
    },

    change:function (event) {
        var c = this.model.toJSON();
        var target = event.target;
        console.log('changing ' + target.id + ' from: ' + target.defaultValue + ' to: ' + target.value);
        $('.option-question-'+c.questionId).prop('disabled', true);
        $('#answer-note-question-'+c.questionId).focus();
        if(c.correct){
            console.log(c.title+" :Bingo!");
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

window.ChoiceListView = Backbone.View.extend({

    tagName: "table",

    render: function(){
        _.each(this.model.models, function (choice) {
            $(this.el).append(new ChoiceListItemView({model:choice}).render().el);
        }, this);
        return this;
    }

});


window.QuestionView = Backbone.View.extend({

    template:_.template($('#tpl-question-item').html()),

    render:function (eventName) {
        var q =  this.model.toJSON();
        var html = $(this.el).html(this.template(q));
        this.choiceListView = new ChoiceListView({model:q.choices});
        html.append(this.choiceListView.render().el);
        html.append("<input type='text' class='span3' placeholder='Type something…' id='answer-note-question-"+q.id+"'>");
        return this;
    },

    close:function () {
        $(this.el).unbind();
        $(this.el).empty();
    }
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


/**  questionDetail ***/

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


window.QuestionDetailView = Backbone.View.extend({

    template:_.template($('#tpl-detail-question-item').html()),

    tagName:"div",

    className: "well",

    initialize:function () {
        this.model.bind("reset", this.render, this);
    },

    render:function (eventName) {
        var q = this.model.toJSON();

        this.choiceDetailListView = new ChoiceDetailListView({model:q.choices});
        $(this.el).html(this.template(q)).append(this.choiceDetailListView.render().el);
        return this;
    },

    close:function () {
        $(this.el).unbind();
        $(this.el).empty();
    }
});

window.ChoiceDetailListView = Backbone.View.extend({

    tagName: "ul",

    render: function(){
        _.each(this.model, function (choice) {
            $(this.el).append(new ChoiceDetailListItemView({model:choice}).render().el);
        }, this);
        return this;
    }

});


window.ChoiceDetailListItemView = Backbone.View.extend({

    tagName:"li",

    template:_.template($('#tpl-detail-question-choice').html()),

    render:function (eventName) {
        var c = this.model;
        $(this.el).html(this.template(c));
        if(c.note){
            if(c.correct){
                $(this.el).append("<div class='alert alert-success'>"+c.note+"</div>");
            }else{
                $(this.el).append("<div class='alert alert-error'>"+c.note+"</div>");
            }
        }
        return this;
    },

    events:{
        "change input":"change",
        "change textarea":"change"
    },

    change:function (event) {
        var target = event.target;
        console.log('changing ' + target.id + ' from: ' + target.defaultValue + ' to: ' + target.value);
        $('.option-question-'+this.model.questionId).prop('disabled', true);
        if(this.model.correct){
            console.log(this.model.title+" :Bingo!");
            $('#i-'+target.id).addClass("icon-ok");
        }else{
            $('#i-'+target.id).addClass("icon-remove");
        }
        // You could change your model on the spot, like this:
        // var change = {};
        // change[target.name] = target.value;
        // this.model.set(change);
    }

});


/**  question comments ***/
window.QComment = Backbone.Model.extend();

window.QCommentCollection = Backbone.Collection.extend({
    model:QComment,
    urlRoot :"/api/qc/"
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
        this.questionDetail = new QuestionDetail({id:id});
        this.questionDetail.fetch({
            success:function(data){
                this.questionDetailView = new QuestionDetailView({model:data});
                $('#content').html(this.questionDetailView.render().el);
                $('#detail-question-desc-'+id).parent().append("loading comment for question...");
            }
        });
    }



});

var app = new AppRouter();
Backbone.history.start();