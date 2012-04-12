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





