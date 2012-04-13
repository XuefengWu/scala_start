

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