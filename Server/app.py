import flask
import pickle
import os
import imageChecker
import imagePredictVGG
from werkzeug.utils import secure_filename
import json
from keras.models import load_model

# Use pickle to load in the pre-trained model.
# with open(f'model/RandomForrest-Fruit.pkl', 'rb') as f:
#     model = pickle.load(f)

model = load_model('./model/vgg16_92.h5')

# Tempat file di upload
UPLOAD_FOLDER = './templates/images/'

app = flask.Flask(__name__, template_folder='templates')
app.secret_key = "super secret key"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/', methods=['GET', 'POST'])
def main():
    if flask.request.method == 'GET':
        return(flask.render_template('index.html'))

    if flask.request.method == 'POST':
        # check if the post request has the file part
        if 'uploaded_file' not in flask.request.files:
            flask.flash('No file part')
            print('No file part')
            # return flask.redirect(flask.request.url)
            return "<h3>"
        file = flask.request.files['uploaded_file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
            flask.flash('No selected file')
            print('No selected file')
            # return flask.redirect(flask.request.url)
            return "<h3>"
        if file:
            filename = secure_filename(file.filename)
            filepath=os.path.join(app.config['UPLOAD_FOLDER'], filename[0:])
            file.save(filepath)

        # Check if the file is an image
        if imageChecker.imagecheck(filepath) :
            # Predict the image
            predict_result = imagePredictVGG.predict(model, filepath)
            result_load = json.loads(predict_result)
            a = ("Result : " + result_load['predict_res'])
            b =  ("Apple Probability: : " + str(result_load['apple_proba']))
            c =  ("Banana Probability :  " + str(result_load['banana_proba']))
            d =  ("Orange Probability :  " + str(result_load['orange_proba']))
            # return "<h3>" + a + "<br></h3>" + "<p>" + b + "<br>" + c + "<br>" + d + "</p>"
            ae = (result_load['predict_res'])
            return ae
        
        else :
            return "<xmp>This file is not an image</xmp>"


if __name__ == '__main__':
    app.run()

# flask run --host=0.0.0.0