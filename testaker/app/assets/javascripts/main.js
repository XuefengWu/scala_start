require("model/question");
require("view/questions");

require("model/questiondetail");
require("view/questiondetails");


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