export class TemplateService {

    static API_URL = "http://localhost:8080/api"

    async getTemplatesList() {
        try {
            const response = await fetch(
                TemplateService.API_URL + "/template"
            )
            return await response.json()
        } catch (error) {
            console.error('Error when invoke getTemplatesList', error)
            throw error
        }
    }

    async addNewTemplate(template) {
        try {
            const response = await fetch(TemplateService.API_URL + "/template", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(template)
            })
            return await response.json()
        } catch (error) {
            console.error('Error when invoke addNewTemplate', error)
            throw error
        }
    }

    async updateTemplate(template, templateId) {
        try {
            const response = await fetch(TemplateService.API_URL + `/template/${templateId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(template)
            })
            return await response.json()
        } catch (error) {
            console.error('Error when invoke addNewTemplate', error)
            throw error
        }
    }

    async getTemplateDetails(templateId) {
        try {
            const response = await fetch(TemplateService.API_URL + `/template/${templateId}`)
            return await response.json()
        } catch (error) {
            console.error('Error when invoke getTemplateDetails', error)
            throw error
        }
    }

}